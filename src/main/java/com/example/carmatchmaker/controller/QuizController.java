package com.example.carmatchmaker.controller;

import com.example.carmatchmaker.dto.BuyerPreferenceForm;
import com.example.carmatchmaker.dto.RecommendationResult;
import com.example.carmatchmaker.enums.*;
import com.example.carmatchmaker.model.BuyerPreference;
import com.example.carmatchmaker.repository.BuyerPreferenceRepository;
import com.example.carmatchmaker.service.RecommendationService;
import com.example.carmatchmaker.service.ShortlistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class QuizController {
    
    private final BuyerPreferenceRepository buyerPreferenceRepository;
    private final RecommendationService recommendationService;
    private final ShortlistService shortlistService;
    
    @GetMapping("/quiz")
    public String showQuiz(Model model) {
        model.addAttribute("preferenceForm", new BuyerPreferenceForm());
        model.addAttribute("useCases", UseCase.values());
        model.addAttribute("bodyTypes", BodyType.values());
        model.addAttribute("fuelTypes", FuelType.values());
        model.addAttribute("transmissions", Transmission.values());
        model.addAttribute("priorities", Priority.values());
        model.addAttribute("mustHaves", MustHave.values());
        return "quiz";
    }
    
    @PostMapping("/quiz")
    public String submitQuiz(@Valid @ModelAttribute("preferenceForm") BuyerPreferenceForm form,
                            BindingResult bindingResult,
                            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("useCases", UseCase.values());
            model.addAttribute("bodyTypes", BodyType.values());
            model.addAttribute("fuelTypes", FuelType.values());
            model.addAttribute("transmissions", Transmission.values());
            model.addAttribute("priorities", Priority.values());
            model.addAttribute("mustHaves", MustHave.values());
            return "quiz";
        }
        
        // Save buyer preference
        BuyerPreference preference = BuyerPreference.builder()
            .budgetMin(form.getBudgetMin())
            .budgetMax(form.getBudgetMax())
            .useCase(form.getUseCase())
            .bodyTypePreference(form.getBodyTypePreference())
            .fuelPreference(form.getFuelPreference())
            .transmissionPreference(form.getTransmissionPreference())
            .priorities(form.getPriorities())
            .mustHaves(form.getMustHaves())
            .build();
        
        preference = buyerPreferenceRepository.save(preference);
        
        return "redirect:/results/" + preference.getId();
    }
    
    @GetMapping("/results/{preferenceId}")
    public String showResults(@PathVariable Long preferenceId, Model model) {
        BuyerPreference preference = buyerPreferenceRepository.findById(preferenceId)
            .orElseThrow(() -> new RuntimeException("Preference not found"));
        
        List<RecommendationResult> recommendations = recommendationService.recommendCars(preference, 5);
        
        model.addAttribute("preference", preference);
        model.addAttribute("recommendations", recommendations);
        model.addAttribute("shortlistService", shortlistService);
        
        return "results";
    }
}
