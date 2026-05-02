package com.example.carmatchmaker.dto;

import com.example.carmatchmaker.enums.BodyType;
import com.example.carmatchmaker.enums.FuelType;
import com.example.carmatchmaker.enums.Transmission;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarFilterRequest {
    
    private Double minPrice;
    private Double maxPrice;
    private String make;
    private BodyType bodyType;
    private FuelType fuelType;
    private Transmission transmission;
    private Double minSafetyRating;
    private String sortBy; // price, mileage, safety, rating
    
    /**
     * Normalize filter values - convert empty/zero values to null for proper query handling
     */
    public void normalize() {
        if (minPrice != null && minPrice <= 0) {
            minPrice = null;
        }
        if (maxPrice != null && maxPrice <= 0) {
            maxPrice = null;
        }
        if (make != null && make.trim().isEmpty()) {
            make = null;
        }
        if (minSafetyRating != null && minSafetyRating <= 0) {
            minSafetyRating = null;
        }
    }
}
