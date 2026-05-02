package com.example.carmatchmaker.controller;

import com.example.carmatchmaker.dto.CarFilterRequest;
import com.example.carmatchmaker.enums.BodyType;
import com.example.carmatchmaker.enums.FuelType;
import com.example.carmatchmaker.enums.Transmission;
import com.example.carmatchmaker.model.Car;
import com.example.carmatchmaker.service.CarService;
import com.example.carmatchmaker.service.ShortlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/cars")
@RequiredArgsConstructor
public class CarController {
    
    private final CarService carService;
    private final ShortlistService shortlistService;
    
    @GetMapping
    public String browseCars(@ModelAttribute CarFilterRequest filter, Model model) {
        List<Car> cars;
        
        if (hasAnyFilter(filter)) {
            cars = carService.filterCars(filter);
        } else {
            cars = carService.getAllCars();
            if (filter.getSortBy() != null && !filter.getSortBy().isEmpty()) {
                // Apply default sorting
                filter.setMinPrice(null);
                filter.setMaxPrice(null);
                cars = carService.filterCars(filter);
            }
        }
        
        model.addAttribute("cars", cars);
        model.addAttribute("filter", filter);
        model.addAttribute("makes", carService.getAllMakes());
        model.addAttribute("bodyTypes", BodyType.values());
        model.addAttribute("fuelTypes", FuelType.values());
        model.addAttribute("transmissions", Transmission.values());
        model.addAttribute("shortlistService", shortlistService);
        
        return "cars";
    }
    
    private boolean hasAnyFilter(CarFilterRequest filter) {
        return filter.getMinPrice() != null ||
               filter.getMaxPrice() != null ||
               filter.getMake() != null ||
               filter.getBodyType() != null ||
               filter.getFuelType() != null ||
               filter.getTransmission() != null ||
               filter.getMinSafetyRating() != null;
    }
}
