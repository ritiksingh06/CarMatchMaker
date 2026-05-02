package com.example.carmatchmaker.service;

import com.example.carmatchmaker.dto.RecommendationResult;
import com.example.carmatchmaker.enums.*;
import com.example.carmatchmaker.model.BuyerPreference;
import com.example.carmatchmaker.model.Car;
import com.example.carmatchmaker.repository.CarRepository;
import com.example.carmatchmaker.repository.ReviewRepository;
import com.example.carmatchmaker.repository.VariantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTests {
    
    @Mock
    private CarRepository carRepository;
    
    @Mock
    private ReviewRepository reviewRepository;
    
    @Mock
    private VariantRepository variantRepository;
    
    @InjectMocks
    private RecommendationService recommendationService;
    
    private Car testCar;
    private BuyerPreference testPreference;
    
    @BeforeEach
    void setUp() {
        // Create test car
        testCar = Car.builder()
            .id(1L)
            .make("Maruti")
            .model("Swift")
            .bodyType(BodyType.HATCHBACK)
            .fuelType(FuelType.PETROL)
            .transmission(Transmission.MANUAL)
            .priceMin(6.5)
            .priceMax(9.5)
            .mileage(23.0)
            .safetyRating(3.0)
            .userRating(4.3)
            .bootSpace(268)
            .seats(5)
            .engine("1.2L K-Series")
            .features("Touchscreen, AC")
            .pros("Fuel efficient, Reliable")
            .cons("Basic features")
            .build();
        
        // Create test preference
        Set<Priority> priorities = new HashSet<>();
        priorities.add(Priority.MILEAGE);
        priorities.add(Priority.LOW_MAINTENANCE);
        
        Set<MustHave> mustHaves = new HashSet<>();
        mustHaves.add(MustHave.GOOD_MILEAGE);
        
        testPreference = BuyerPreference.builder()
            .budgetMin(5.0)
            .budgetMax(10.0)
            .useCase(UseCase.CITY_COMMUTE)
            .bodyTypePreference(BodyType.HATCHBACK)
            .fuelPreference(FuelType.PETROL)
            .transmissionPreference(Transmission.MANUAL)
            .priorities(priorities)
            .mustHaves(mustHaves)
            .build();
    }
    
    @Test
    void testRecommendCars_ReturnsTopMatches() {
        // Given
        when(carRepository.findAll()).thenReturn(List.of(testCar));
        when(reviewRepository.findByCarId(1L)).thenReturn(List.of());
        when(variantRepository.findByCarId(1L)).thenReturn(List.of());
        
        // When
        List<RecommendationResult> results = recommendationService.recommendCars(testPreference, 5);
        
        // Then
        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
        
        RecommendationResult result = results.get(0);
        assertEquals(testCar, result.getCar());
        assertTrue(result.getScore() > 0);
        assertFalse(result.getMatchReasons().isEmpty());
    }
    
    @Test
    void testScoring_BudgetFit() {
        // Given: Car perfectly within budget
        when(carRepository.findAll()).thenReturn(List.of(testCar));
        when(reviewRepository.findByCarId(1L)).thenReturn(List.of());
        when(variantRepository.findByCarId(1L)).thenReturn(List.of());
        
        // When
        List<RecommendationResult> results = recommendationService.recommendCars(testPreference, 1);
        
        // Then
        assertNotNull(results);
        assertFalse(results.isEmpty());
        // Score should be high since car fits budget, use case, and preferences
        assertTrue(results.get(0).getScore() > 50, "Score should be above 50 for good match");
    }
    
    @Test
    void testScoring_UseCaseCityCommute() {
        // Given: Hatchback with good mileage for city commute
        when(carRepository.findAll()).thenReturn(List.of(testCar));
        when(reviewRepository.findByCarId(1L)).thenReturn(List.of());
        when(variantRepository.findByCarId(1L)).thenReturn(List.of());
        
        // When
        List<RecommendationResult> results = recommendationService.recommendCars(testPreference, 1);
        
        // Then
        RecommendationResult result = results.get(0);
        assertTrue(result.getMatchReasons().stream()
            .anyMatch(reason -> reason.toLowerCase().contains("city")),
            "Should mention city commute fit");
    }
    
    @Test
    void testScoring_MileageBoost() {
        // Given: Car with good mileage and mileage as priority
        when(carRepository.findAll()).thenReturn(List.of(testCar));
        when(reviewRepository.findByCarId(1L)).thenReturn(List.of());
        when(variantRepository.findByCarId(1L)).thenReturn(List.of());
        
        // When
        List<RecommendationResult> results = recommendationService.recommendCars(testPreference, 1);
        
        // Then
        RecommendationResult result = results.get(0);
        assertTrue(result.getMatchReasons().stream()
            .anyMatch(reason -> reason.toLowerCase().contains("mileage")),
            "Should highlight mileage as a match reason");
    }
    
    @Test
    void testScoring_MustHaveSatisfied() {
        // Given: Car satisfies must-have (good mileage)
        when(carRepository.findAll()).thenReturn(List.of(testCar));
        when(reviewRepository.findByCarId(1L)).thenReturn(List.of());
        when(variantRepository.findByCarId(1L)).thenReturn(List.of());
        
        // When
        List<RecommendationResult> results = recommendationService.recommendCars(testPreference, 1);
        
        // Then
        RecommendationResult result = results.get(0);
        assertTrue(result.getMatchReasons().stream()
            .anyMatch(reason -> reason.toLowerCase().contains("good mileage")),
            "Should mention must-have is satisfied");
    }
    
    @Test
    void testScoring_BudgetOutOfRange() {
        // Given: Budget too low for car
        testPreference.setBudgetMin(3.0);
        testPreference.setBudgetMax(5.0);
        
        when(carRepository.findAll()).thenReturn(List.of(testCar));
        when(reviewRepository.findByCarId(1L)).thenReturn(List.of());
        when(variantRepository.findByCarId(1L)).thenReturn(List.of());
        
        // When
        List<RecommendationResult> results = recommendationService.recommendCars(testPreference, 1);
        
        // Then
        RecommendationResult result = results.get(0);
        // Score should be lower due to budget mismatch
        assertTrue(result.getScore() < 80, "Score should be lower for budget mismatch");
    }
    
    @Test
    void testScoring_BodyTypeMismatch() {
        // Given: Preference for SUV but car is Hatchback
        testPreference.setBodyTypePreference(BodyType.SUV);
        
        when(carRepository.findAll()).thenReturn(List.of(testCar));
        when(reviewRepository.findByCarId(1L)).thenReturn(List.of());
        when(variantRepository.findByCarId(1L)).thenReturn(List.of());
        
        // When
        List<RecommendationResult> results = recommendationService.recommendCars(testPreference, 1);
        
        // Then
        RecommendationResult result = results.get(0);
        // Should still return result but score reduced
        assertNotNull(result);
        assertTrue(result.getScore() > 0);
    }
    
    @Test
    void testRecommendations_SortedByScore() {
        // Given: Multiple cars with different fits
        Car car1 = Car.builder()
            .id(1L).make("Maruti").model("Swift")
            .bodyType(BodyType.HATCHBACK).fuelType(FuelType.PETROL)
            .transmission(Transmission.MANUAL).priceMin(6.5).priceMax(9.5)
            .mileage(23.0).safetyRating(3.0).userRating(4.3)
            .bootSpace(268).seats(5).engine("1.2L")
            .features("Basic").pros("Efficient").cons("Basic")
            .build();
        
        Car car2 = Car.builder()
            .id(2L).make("Tata").model("Punch")
            .bodyType(BodyType.COMPACT_SUV).fuelType(FuelType.PETROL)
            .transmission(Transmission.MANUAL).priceMin(6.0).priceMax(10.0)
            .mileage(18.8).safetyRating(5.0).userRating(4.2)
            .bootSpace(366).seats(5).engine("1.2L")
            .features("Safety").pros("Safe").cons("Noisy")
            .build();
        
        when(carRepository.findAll()).thenReturn(List.of(car1, car2));
        when(reviewRepository.findByCarId(1L)).thenReturn(List.of());
        when(reviewRepository.findByCarId(2L)).thenReturn(List.of());
        when(variantRepository.findByCarId(1L)).thenReturn(List.of());
        when(variantRepository.findByCarId(2L)).thenReturn(List.of());
        
        // When
        List<RecommendationResult> results = recommendationService.recommendCars(testPreference, 2);
        
        // Then
        assertEquals(2, results.size());
        // Results should be sorted by score descending
        assertTrue(results.get(0).getScore() >= results.get(1).getScore(),
            "Results should be sorted by score in descending order");
    }
}
