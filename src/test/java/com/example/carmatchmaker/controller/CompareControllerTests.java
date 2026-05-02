package com.example.carmatchmaker.controller;

import com.example.carmatchmaker.enums.*;
import com.example.carmatchmaker.model.BuyerPreference;
import com.example.carmatchmaker.model.Car;
import com.example.carmatchmaker.repository.BuyerPreferenceRepository;
import com.example.carmatchmaker.service.ShortlistService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CompareController.class)
class CompareControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ShortlistService shortlistService;

    @MockBean
    private BuyerPreferenceRepository buyerPreferenceRepository;

    private Car createCar(Long id, String make, String model, double priceMin,
                          double mileage, double safety, double rating, Integer bootSpace) {
        return Car.builder()
                .id(id).make(make).model(model)
                .bodyType(BodyType.SUV).fuelType(FuelType.PETROL)
                .transmission(Transmission.MANUAL)
                .priceMin(priceMin).priceMax(priceMin + 5.0)
                .mileage(mileage).safetyRating(safety).userRating(rating)
                .bootSpace(bootSpace).seats(5).engine("1.2L")
                .features("").pros("").cons("")
                .build();
    }

    @Test
    @DisplayName("GET /compare with empty shortlist should show error message")
    void compareEmptyShortlist() throws Exception {
        when(shortlistService.getShortlistedCars()).thenReturn(List.of());

        mockMvc.perform(get("/compare"))
                .andExpect(status().isOk())
                .andExpect(view().name("compare"))
                .andExpect(model().attribute("error", "Your shortlist is empty. Add cars to compare."));
    }

    @Test
    @DisplayName("GET /compare with cars should calculate all winners")
    void compareWithCars() throws Exception {
        Car cheap = createCar(1L, "Maruti", "Swift", 5.0, 23.0, 3.0, 4.0, 268);
        Car safe = createCar(2L, "Tata", "Nexon", 8.0, 17.0, 5.0, 4.5, 350);
        Car spacious = createCar(3L, "Hyundai", "Creta", 11.0, 17.0, 4.0, 4.3, 433);

        when(shortlistService.getShortlistedCars()).thenReturn(List.of(cheap, safe, spacious));

        mockMvc.perform(get("/compare"))
                .andExpect(status().isOk())
                .andExpect(view().name("compare"))
                .andExpect(model().attribute("cars", List.of(cheap, safe, spacious)))
                .andExpect(model().attribute("cheapest", cheap))
                .andExpect(model().attribute("bestMileage", cheap))
                .andExpect(model().attribute("safest", safe))
                .andExpect(model().attribute("bestRated", safe))
                .andExpect(model().attribute("largestBoot", spacious));
    }

    @Test
    @DisplayName("GET /compare with preferenceId should load preference")
    void compareWithPreference() throws Exception {
        Car car = createCar(1L, "Maruti", "Swift", 5.0, 23.0, 3.0, 4.0, 268);
        when(shortlistService.getShortlistedCars()).thenReturn(List.of(car));

        BuyerPreference pref = BuyerPreference.builder()
                .id(1L).budgetMin(5.0).budgetMax(15.0)
                .useCase(UseCase.CITY_COMMUTE)
                .bodyTypePreference(BodyType.HATCHBACK)
                .fuelPreference(FuelType.PETROL)
                .transmissionPreference(Transmission.MANUAL)
                .build();
        when(buyerPreferenceRepository.findById(1L)).thenReturn(Optional.of(pref));

        mockMvc.perform(get("/compare").param("preferenceId", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("compare"))
                .andExpect(model().attribute("preference", pref));
    }

    @Test
    @DisplayName("GET /compare with invalid preferenceId should still show cars")
    void compareWithInvalidPreference() throws Exception {
        Car car = createCar(1L, "Maruti", "Swift", 5.0, 23.0, 3.0, 4.0, 268);
        when(shortlistService.getShortlistedCars()).thenReturn(List.of(car));
        when(buyerPreferenceRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/compare").param("preferenceId", "99"))
                .andExpect(status().isOk())
                .andExpect(view().name("compare"))
                .andExpect(model().attributeDoesNotExist("preference"));
    }

    @Test
    @DisplayName("GET /compare with null boot space should handle gracefully")
    void compareWithNullBootSpace() throws Exception {
        Car carNoBootSpace = createCar(1L, "Test", "Car", 5.0, 20.0, 4.0, 4.0, null);

        when(shortlistService.getShortlistedCars()).thenReturn(List.of(carNoBootSpace));

        mockMvc.perform(get("/compare"))
                .andExpect(status().isOk())
                .andExpect(view().name("compare"))
                .andExpect(model().attribute("largestBoot", (Object) null));
    }
}
