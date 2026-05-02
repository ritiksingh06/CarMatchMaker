package com.example.carmatchmaker.controller;

import com.example.carmatchmaker.enums.*;
import com.example.carmatchmaker.model.BuyerPreference;
import com.example.carmatchmaker.repository.BuyerPreferenceRepository;
import com.example.carmatchmaker.service.RecommendationService;
import com.example.carmatchmaker.service.ShortlistService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(QuizController.class)
class QuizControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BuyerPreferenceRepository buyerPreferenceRepository;

    @MockBean
    private RecommendationService recommendationService;

    @MockBean
    private ShortlistService shortlistService;

    @Test
    @DisplayName("GET /quiz should show quiz page with enum values")
    void showQuiz() throws Exception {
        mockMvc.perform(get("/quiz"))
                .andExpect(status().isOk())
                .andExpect(view().name("quiz"))
                .andExpect(model().attributeExists("preferenceForm", "useCases", "bodyTypes",
                        "fuelTypes", "transmissions", "priorities", "mustHaves"));
    }

    @Test
    @DisplayName("POST /quiz with valid form should redirect to results")
    void submitQuizValid() throws Exception {
        BuyerPreference savedPref = BuyerPreference.builder()
                .id(1L)
                .budgetMin(5.0)
                .budgetMax(15.0)
                .useCase(UseCase.CITY_COMMUTE)
                .bodyTypePreference(BodyType.HATCHBACK)
                .fuelPreference(FuelType.PETROL)
                .transmissionPreference(Transmission.MANUAL)
                .build();

        when(buyerPreferenceRepository.save(any(BuyerPreference.class))).thenReturn(savedPref);

        mockMvc.perform(post("/quiz")
                        .param("budgetMin", "5.0")
                        .param("budgetMax", "15.0")
                        .param("useCase", "CITY_COMMUTE")
                        .param("bodyTypePreference", "HATCHBACK")
                        .param("fuelPreference", "PETROL")
                        .param("transmissionPreference", "MANUAL"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/results/1"));
    }

    @Test
    @DisplayName("POST /quiz with missing required fields should return quiz with errors")
    void submitQuizInvalid() throws Exception {
        mockMvc.perform(post("/quiz")
                        .param("budgetMin", "")
                        .param("budgetMax", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("quiz"))
                .andExpect(model().hasErrors());
    }

    @Test
    @DisplayName("POST /quiz with all fields empty should show validation errors")
    void submitQuizAllEmpty() throws Exception {
        mockMvc.perform(post("/quiz"))
                .andExpect(status().isOk())
                .andExpect(view().name("quiz"))
                .andExpect(model().attributeHasFieldErrors("preferenceForm",
                        "budgetMin", "budgetMax", "useCase", "bodyTypePreference",
                        "fuelPreference", "transmissionPreference"));
    }

    @Test
    @DisplayName("GET /results/{id} should show recommendations")
    void showResults() throws Exception {
        BuyerPreference pref = BuyerPreference.builder()
                .id(1L)
                .budgetMin(5.0).budgetMax(15.0)
                .useCase(UseCase.CITY_COMMUTE)
                .bodyTypePreference(BodyType.HATCHBACK)
                .fuelPreference(FuelType.PETROL)
                .transmissionPreference(Transmission.MANUAL)
                .build();

        when(buyerPreferenceRepository.findById(1L)).thenReturn(Optional.of(pref));
        when(recommendationService.recommendCars(any(BuyerPreference.class), eq(5)))
                .thenReturn(List.of());

        mockMvc.perform(get("/results/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("results"))
                .andExpect(model().attributeExists("preference", "recommendations", "shortlistService"));
    }

    @Test
    @DisplayName("GET /results/{id} with non-existent ID should throw error")
    void showResultsNotFound() {
        when(buyerPreferenceRepository.findById(99L)).thenReturn(Optional.empty());

        org.junit.jupiter.api.Assertions.assertThrows(Exception.class, () ->
                mockMvc.perform(get("/results/99")));
    }
}
