package com.example.carmatchmaker.dto;

import com.example.carmatchmaker.enums.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BuyerPreferenceForm {
    
    @NotNull(message = "Minimum budget is required")
    private Double budgetMin;
    
    @NotNull(message = "Maximum budget is required")
    private Double budgetMax;
    
    @NotNull(message = "Primary use case is required")
    private UseCase useCase;
    
    @NotNull(message = "Body type preference is required")
    private BodyType bodyTypePreference;
    
    @NotNull(message = "Fuel preference is required")
    private FuelType fuelPreference;
    
    @NotNull(message = "Transmission preference is required")
    private Transmission transmissionPreference;
    
    @Builder.Default
    private Set<Priority> priorities = new HashSet<>();
    
    @Builder.Default
    private Set<MustHave> mustHaves = new HashSet<>();
}
