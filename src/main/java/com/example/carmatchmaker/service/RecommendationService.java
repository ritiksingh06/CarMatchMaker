package com.example.carmatchmaker.service;

import com.example.carmatchmaker.dto.RecommendationResult;
import com.example.carmatchmaker.enums.*;
import com.example.carmatchmaker.model.BuyerPreference;
import com.example.carmatchmaker.model.Car;
import com.example.carmatchmaker.model.Review;
import com.example.carmatchmaker.model.Variant;
import com.example.carmatchmaker.repository.CarRepository;
import com.example.carmatchmaker.repository.ReviewRepository;
import com.example.carmatchmaker.repository.VariantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecommendationService {
    
    private final CarRepository carRepository;
    private final ReviewRepository reviewRepository;
    private final VariantRepository variantRepository;
    
    /**
     * Score cars based on buyer preferences using transparent algorithm:
     * - Budget fit: 25 points
     * - Use-case fit: 20 points
     * - Mileage: 15 points
     * - Safety rating: 15 points
     * - User rating/review sentiment: 10 points
     * - Body type match: 5 points
     * - Fuel/transmission match: 5 points
     * - Must-have match: 5 points
     */
    public List<RecommendationResult> recommendCars(BuyerPreference preference, int topN) {
        List<Car> allCars = carRepository.findAll();
        
        List<RecommendationResult> results = allCars.stream()
            .map(car -> scoreCarAgainstPreference(car, preference))
            .sorted(Comparator.comparing(RecommendationResult::getScore).reversed())
            .limit(topN)
            .toList();
        
        return results;
    }
    
    private RecommendationResult scoreCarAgainstPreference(Car car, BuyerPreference pref) {
        double totalScore = 0.0;
        List<String> matchReasons = new ArrayList<>();
        List<String> tradeoffs = new ArrayList<>();
        
        // 1. Budget fit (25 points)
        double budgetScore = calculateBudgetScore(car, pref.getBudgetMin(), pref.getBudgetMax());
        totalScore += budgetScore;
        if (budgetScore >= 20) {
            matchReasons.add("Perfect budget fit: " + car.getPriceRange());
        } else if (budgetScore >= 12) {
            matchReasons.add("Good budget fit within range");
        } else {
            tradeoffs.add("Budget might be tight - consider base variant");
        }
        
        // 2. Use-case fit (20 points)
        double useCaseScore = calculateUseCaseScore(car, pref.getUseCase());
        totalScore += useCaseScore;
        addUseCaseReason(pref.getUseCase(), car, useCaseScore, matchReasons, tradeoffs);
        
        // 3. Mileage (15 points)
        double mileageScore = calculateMileageScore(car, pref.getPriorities());
        totalScore += mileageScore;
        if (mileageScore >= 12) {
            matchReasons.add("Excellent mileage: " + car.getMileage() + " km/l");
        } else if (car.getMileage() < 12) {
            tradeoffs.add("Lower mileage - higher fuel costs");
        }
        
        // 4. Safety rating (15 points)
        double safetyScore = calculateSafetyScore(car, pref.getPriorities());
        totalScore += safetyScore;
        if (safetyScore >= 12) {
            matchReasons.add("High safety rating: " + car.getSafetyRating() + "/5 stars");
        }
        
        // 5. User rating and reviews (10 points)
        double reviewScore = calculateReviewScore(car, pref.getPriorities());
        totalScore += reviewScore;
        if (car.getUserRating() >= 4.0) {
            matchReasons.add("Highly rated by users: " + car.getUserRating() + "/5");
        }
        
        // 6. Body type match (5 points)
        double bodyTypeScore = calculateBodyTypeScore(car, pref.getBodyTypePreference());
        totalScore += bodyTypeScore;
        if (bodyTypeScore >= 4 && pref.getBodyTypePreference() != BodyType.NO_PREFERENCE) {
            matchReasons.add("Matches preferred body type: " + car.getBodyType().getDisplayName());
        }
        
        // 7. Fuel/transmission match (5 points)
        double fuelTransScore = calculateFuelTransmissionScore(car, pref);
        totalScore += fuelTransScore;
        if (pref.getFuelPreference() != FuelType.NO_PREFERENCE && 
            car.getFuelType() == pref.getFuelPreference()) {
            matchReasons.add("Preferred fuel type: " + car.getFuelType().getDisplayName());
        }
        if (pref.getTransmissionPreference() != Transmission.NO_PREFERENCE && 
            car.getTransmission() == pref.getTransmissionPreference()) {
            matchReasons.add("Preferred transmission: " + car.getTransmission().getDisplayName());
        }
        
        // 8. Must-have match (5 points)
        double mustHaveScore = calculateMustHaveScore(car, pref.getMustHaves());
        totalScore += mustHaveScore;
        addMustHaveReasons(car, pref.getMustHaves(), mustHaveScore, matchReasons, tradeoffs);
        
        // Find best variant
        Variant bestVariant = findBestVariant(car, pref);
        
        return RecommendationResult.builder()
            .car(car)
            .score(totalScore)
            .matchReasons(matchReasons)
            .tradeoffs(tradeoffs)
            .bestVariant(bestVariant)
            .build();
    }
    
    private double calculateBudgetScore(Car car, Double minBudget, Double maxBudget) {
        // Perfect fit: car's price range overlaps significantly with budget
        if (car.getPriceMin() >= minBudget && car.getPriceMax() <= maxBudget) {
            return 25.0; // Perfect fit
        }
        
        // Good fit: some variants are within budget
        if (car.getPriceMin() <= maxBudget && car.getPriceMax() >= minBudget) {
            double overlap = Math.min(car.getPriceMax(), maxBudget) - Math.max(car.getPriceMin(), minBudget);
            double carRange = car.getPriceMax() - car.getPriceMin();
            return 25.0 * (overlap / carRange);
        }
        
        // Poor fit: outside budget
        double distance = car.getPriceMin() > maxBudget ? 
            (car.getPriceMin() - maxBudget) : (minBudget - car.getPriceMax());
        return Math.max(0, 10.0 - distance * 2);
    }
    
    private double calculateUseCaseScore(Car car, UseCase useCase) {
        return switch (useCase) {
            case CITY_COMMUTE -> {
                // Prefer hatchbacks, good mileage, compact size
                double score = 0.0;
                if (car.getBodyType() == BodyType.HATCHBACK) score += 10.0;
                else if (car.getBodyType() == BodyType.COMPACT_SUV) score += 7.0;
                else score += 3.0;
                
                if (car.getMileage() >= 18) score += 10.0;
                else if (car.getMileage() >= 14) score += 7.0;
                else score += 3.0;
                yield score;
            }
            case FAMILY -> {
                // Prefer space, safety, comfort
                double score = 0.0;
                if (car.getBodyType() == BodyType.SUV || car.getBodyType() == BodyType.MPV) score += 10.0;
                else if (car.getBodyType() == BodyType.SEDAN) score += 7.0;
                else score += 3.0;
                
                if (car.getSeats() >= 7) score += 5.0;
                else if (car.getSeats() >= 5) score += 3.0;
                
                if (car.getSafetyRating() >= 4.0) score += 5.0;
                else if (car.getSafetyRating() >= 3.0) score += 3.0;
                yield score;
            }
            case HIGHWAY -> {
                // Prefer power, stability, comfort
                double score = 0.0;
                if (car.getBodyType() == BodyType.SEDAN || car.getBodyType() == BodyType.SUV) score += 10.0;
                else score += 5.0;
                
                if (car.getSafetyRating() >= 4.0) score += 5.0;
                if (car.getUserRating() >= 4.0) score += 5.0;
                yield score;
            }
            case PERFORMANCE -> {
                // Prefer power, handling
                double score = 0.0;
                if (car.getBodyType() == BodyType.SEDAN || car.getBodyType() == BodyType.HATCHBACK) score += 10.0;
                else score += 5.0;
                
                if (car.getUserRating() >= 4.0) score += 10.0;
                else score += 5.0;
                yield score;
            }
            case FIRST_CAR -> {
                // Prefer affordable, easy to drive, low maintenance
                double score = 0.0;
                if (car.getPriceMin() <= 8.0) score += 10.0;
                else if (car.getPriceMin() <= 12.0) score += 7.0;
                else score += 3.0;
                
                if (car.getBodyType() == BodyType.HATCHBACK) score += 5.0;
                if (car.getMileage() >= 16) score += 5.0;
                yield score;
            }
        };
    }
    
    private double calculateMileageScore(Car car, java.util.Set<Priority> priorities) {
        double baseScore = Math.min(15.0, (car.getMileage() / 25.0) * 15.0);
        
        // Boost if mileage is a priority
        if (priorities.contains(Priority.MILEAGE)) {
            baseScore = Math.min(15.0, baseScore * 1.2);
        }
        
        return baseScore;
    }
    
    private double calculateSafetyScore(Car car, java.util.Set<Priority> priorities) {
        double baseScore = (car.getSafetyRating() / 5.0) * 15.0;
        
        // Boost if safety is a priority
        if (priorities.contains(Priority.SAFETY)) {
            baseScore = Math.min(15.0, baseScore * 1.2);
        }
        
        return baseScore;
    }
    
    private double calculateReviewScore(Car car, java.util.Set<Priority> priorities) {
        double userRatingScore = (car.getUserRating() / 5.0) * 10.0;
        
        // Factor in review sentiment if available
        List<Review> reviews = reviewRepository.findByCarId(car.getId());
        if (!reviews.isEmpty()) {
            double avgSentiment = reviews.stream()
                .mapToDouble(Review::getSentimentScore)
                .average()
                .orElse(0.0);
            
            // Normalize sentiment from [-1, 1] to [0, 1]
            double sentimentScore = (avgSentiment + 1.0) / 2.0;
            userRatingScore = (userRatingScore * 0.7) + (sentimentScore * 10.0 * 0.3);
        }
        
        return userRatingScore;
    }
    
    private double calculateBodyTypeScore(Car car, BodyType preference) {
        if (preference == BodyType.NO_PREFERENCE) {
            return 5.0; // Neutral score
        }
        
        return car.getBodyType() == preference ? 5.0 : 0.0;
    }
    
    private double calculateFuelTransmissionScore(Car car, BuyerPreference pref) {
        double score = 0.0;
        
        if (pref.getFuelPreference() == FuelType.NO_PREFERENCE || 
            car.getFuelType() == pref.getFuelPreference()) {
            score += 2.5;
        }
        
        if (pref.getTransmissionPreference() == Transmission.NO_PREFERENCE || 
            car.getTransmission() == pref.getTransmissionPreference()) {
            score += 2.5;
        }
        
        return score;
    }
    
    private double calculateMustHaveScore(Car car, java.util.Set<MustHave> mustHaves) {
        if (mustHaves.isEmpty()) {
            return 5.0; // No must-haves, full score
        }
        
        int satisfiedCount = 0;
        for (MustHave mustHave : mustHaves) {
            boolean satisfied = switch (mustHave) {
                case AUTOMATIC_TRANSMISSION -> car.getTransmission() == Transmission.AUTOMATIC;
                case HIGH_SAFETY_RATING -> car.getSafetyRating() >= 4.0;
                case LARGE_BOOT -> car.getBootSpace() != null && car.getBootSpace() >= 400;
                case GOOD_MILEAGE -> car.getMileage() >= 18.0;
                case SUNROOF, CONNECTED_FEATURES -> 
                    car.getFeatures() != null && 
                    car.getFeatures().toLowerCase().contains(mustHave.name().toLowerCase().replace("_", " "));
            };
            
            if (satisfied) satisfiedCount++;
        }
        
        return (5.0 * satisfiedCount) / mustHaves.size();
    }
    
    private void addUseCaseReason(UseCase useCase, Car car, double score, 
                                   List<String> matchReasons, List<String> tradeoffs) {
        if (score >= 15) {
            matchReasons.add("Ideal for " + useCase.getDisplayName().toLowerCase());
        } else if (score >= 10) {
            matchReasons.add("Good for " + useCase.getDisplayName().toLowerCase());
        } else {
            tradeoffs.add("Not optimized for " + useCase.getDisplayName().toLowerCase());
        }
    }
    
    private void addMustHaveReasons(Car car, java.util.Set<MustHave> mustHaves, double score,
                                     List<String> matchReasons, List<String> tradeoffs) {
        if (mustHaves.isEmpty()) return;
        
        for (MustHave mustHave : mustHaves) {
            boolean satisfied = switch (mustHave) {
                case AUTOMATIC_TRANSMISSION -> car.getTransmission() == Transmission.AUTOMATIC;
                case HIGH_SAFETY_RATING -> car.getSafetyRating() >= 4.0;
                case LARGE_BOOT -> car.getBootSpace() != null && car.getBootSpace() >= 400;
                case GOOD_MILEAGE -> car.getMileage() >= 18.0;
                case SUNROOF, CONNECTED_FEATURES -> 
                    car.getFeatures() != null && 
                    car.getFeatures().toLowerCase().contains(mustHave.name().toLowerCase().replace("_", " "));
            };
            
            if (satisfied) {
                matchReasons.add("Has " + mustHave.getDisplayName().toLowerCase());
            } else {
                tradeoffs.add("Missing " + mustHave.getDisplayName().toLowerCase());
            }
        }
    }
    
    private Variant findBestVariant(Car car, BuyerPreference pref) {
        List<Variant> variants = variantRepository.findByCarId(car.getId());
        if (variants.isEmpty()) return null;
        
        // Find variant that best matches preferences and budget
        return variants.stream()
            .filter(v -> v.getPrice() >= pref.getBudgetMin() && v.getPrice() <= pref.getBudgetMax())
            .min(Comparator.comparing(Variant::getPrice))
            .orElse(variants.stream()
                .min(Comparator.comparing(Variant::getPrice))
                .orElse(null));
    }
}
