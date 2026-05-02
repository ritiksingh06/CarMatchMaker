package com.example.carmatchmaker.controller;

import com.example.carmatchmaker.model.BuyerPreference;
import com.example.carmatchmaker.model.Car;
import com.example.carmatchmaker.repository.BuyerPreferenceRepository;
import com.example.carmatchmaker.service.ShortlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/compare")
@RequiredArgsConstructor
public class CompareController {
    
    private final ShortlistService shortlistService;
    private final BuyerPreferenceRepository buyerPreferenceRepository;
    
    @GetMapping
    public String compareCars(@RequestParam(required = false) Long preferenceId, Model model) {
        List<Car> shortlistedCars = shortlistService.getShortlistedCars();
        
        if (shortlistedCars.isEmpty()) {
            model.addAttribute("error", "Your shortlist is empty. Add cars to compare.");
            return "compare";
        }
        
        model.addAttribute("cars", shortlistedCars);
        
        // Determine winners for each metric
        Car cheapest = shortlistedCars.stream()
            .min(Comparator.comparing(Car::getPriceMin))
            .orElse(null);
        
        Car bestMileage = shortlistedCars.stream()
            .max(Comparator.comparing(Car::getMileage))
            .orElse(null);
        
        Car safest = shortlistedCars.stream()
            .max(Comparator.comparing(Car::getSafetyRating))
            .orElse(null);
        
        Car bestRated = shortlistedCars.stream()
            .max(Comparator.comparing(Car::getUserRating))
            .orElse(null);
        
        Car largestBoot = shortlistedCars.stream()
            .filter(c -> c.getBootSpace() != null)
            .max(Comparator.comparing(Car::getBootSpace))
            .orElse(null);
        
        model.addAttribute("cheapest", cheapest);
        model.addAttribute("bestMileage", bestMileage);
        model.addAttribute("safest", safest);
        model.addAttribute("bestRated", bestRated);
        model.addAttribute("largestBoot", largestBoot);
        
        // Get buyer preference if available
        if (preferenceId != null) {
            Optional<BuyerPreference> preference = buyerPreferenceRepository.findById(preferenceId);
            preference.ifPresent(pref -> model.addAttribute("preference", pref));
        }
        
        return "compare";
    }
}
