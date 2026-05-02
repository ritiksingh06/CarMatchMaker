package com.example.carmatchmaker.controller;

import com.example.carmatchmaker.model.Car;
import com.example.carmatchmaker.service.ShortlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/shortlist")
@RequiredArgsConstructor
public class ShortlistController {
    
    private final ShortlistService shortlistService;
    
    @GetMapping
    public String viewShortlist(Model model) {
        List<Car> shortlistedCars = shortlistService.getShortlistedCars();
        model.addAttribute("cars", shortlistedCars);
        model.addAttribute("shortlistService", shortlistService);
        return "shortlist";
    }
    
    @PostMapping("/{carId}")
    public String addToShortlist(@PathVariable Long carId,
                                 @RequestHeader(value = "HX-Request", required = false) String htmxRequest) {
        shortlistService.addToShortlist(carId);
        
        if (htmxRequest != null) {
            // HTMX request - return partial update
            return "fragments/car-card :: shortlist-button";
        }
        
        return "redirect:/shortlist";
    }
    
    @PostMapping("/{carId}/remove")
    public String removeFromShortlist(@PathVariable Long carId,
                                     @RequestHeader(value = "HX-Request", required = false) String htmxRequest) {
        shortlistService.removeFromShortlist(carId);
        
        if (htmxRequest != null) {
            // HTMX request - return partial update
            return "fragments/car-card :: shortlist-button";
        }
        
        return "redirect:/shortlist";
    }
}
