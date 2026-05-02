package com.example.carmatchmaker.controller;

import com.example.carmatchmaker.enums.*;
import com.example.carmatchmaker.model.Car;
import com.example.carmatchmaker.service.CarService;
import com.example.carmatchmaker.service.ShortlistService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CarController.class)
class CarControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CarService carService;

    @MockBean
    private ShortlistService shortlistService;

    private Car createTestCar(Long id, String make, String model) {
        return Car.builder()
                .id(id).make(make).model(model)
                .bodyType(BodyType.HATCHBACK).fuelType(FuelType.PETROL)
                .transmission(Transmission.MANUAL)
                .priceMin(6.5).priceMax(9.5)
                .mileage(23.0).safetyRating(3.0).userRating(4.3)
                .bootSpace(268).seats(5).engine("1.2L")
                .features("Touchscreen").pros("Good").cons("Basic")
                .build();
    }

    @Test
    @DisplayName("GET /cars should return cars page with all cars when no filters")
    void browseCarsNoFilters() throws Exception {
        Car swift = createTestCar(1L, "Maruti", "Swift");
        when(carService.getAllCars()).thenReturn(List.of(swift));
        when(carService.getAllMakes()).thenReturn(List.of("Maruti"));
        when(shortlistService.isCarInShortlist(1L)).thenReturn(false);

        mockMvc.perform(get("/cars"))
                .andExpect(status().isOk())
                .andExpect(view().name("cars"))
                .andExpect(model().attributeExists("cars", "filter", "makes", "bodyTypes", "fuelTypes", "transmissions"))
                .andExpect(model().attribute("cars", List.of(swift)));
    }

    @Test
    @DisplayName("GET /cars with price filter should apply filter")
    void browseCarsWithPriceFilter() throws Exception {
        Car swift = createTestCar(1L, "Maruti", "Swift");
        when(carService.filterCars(any())).thenReturn(List.of(swift));
        when(carService.getAllMakes()).thenReturn(List.of("Maruti"));
        when(shortlistService.isCarInShortlist(1L)).thenReturn(false);

        mockMvc.perform(get("/cars")
                        .param("minPrice", "5.0")
                        .param("maxPrice", "10.0"))
                .andExpect(status().isOk())
                .andExpect(view().name("cars"))
                .andExpect(model().attributeExists("cars"));
    }

    @Test
    @DisplayName("GET /cars with bodyType filter should apply filter")
    void browseCarsWithBodyTypeFilter() throws Exception {
        when(carService.filterCars(any())).thenReturn(List.of());
        when(carService.getAllMakes()).thenReturn(List.of());

        mockMvc.perform(get("/cars")
                        .param("bodyType", "SUV"))
                .andExpect(status().isOk())
                .andExpect(view().name("cars"));
    }

    @Test
    @DisplayName("GET /cars with sortBy only should apply sorting")
    void browseCarsWithSortOnly() throws Exception {
        Car swift = createTestCar(1L, "Maruti", "Swift");
        when(carService.getAllCars()).thenReturn(List.of(swift));
        when(carService.filterCars(any())).thenReturn(List.of(swift));
        when(carService.getAllMakes()).thenReturn(List.of("Maruti"));
        when(shortlistService.isCarInShortlist(1L)).thenReturn(false);

        mockMvc.perform(get("/cars")
                        .param("sortBy", "price"))
                .andExpect(status().isOk())
                .andExpect(view().name("cars"));
    }

    @Test
    @DisplayName("GET /cars with empty filters should show all cars")
    void browseCarsWithEmptyFilters() throws Exception {
        when(carService.getAllCars()).thenReturn(List.of());
        when(carService.getAllMakes()).thenReturn(List.of());

        mockMvc.perform(get("/cars")
                        .param("make", "")
                        .param("sortBy", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("cars"));
    }

    @Test
    @DisplayName("GET /cars should populate model with enum values for dropdowns")
    void browseCarsPopulatesEnumValues() throws Exception {
        when(carService.getAllCars()).thenReturn(List.of());
        when(carService.getAllMakes()).thenReturn(List.of("Maruti", "Tata"));

        mockMvc.perform(get("/cars"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("makes", List.of("Maruti", "Tata")))
                .andExpect(model().attribute("bodyTypes", BodyType.values()))
                .andExpect(model().attribute("fuelTypes", FuelType.values()))
                .andExpect(model().attribute("transmissions", Transmission.values()));
    }
}
