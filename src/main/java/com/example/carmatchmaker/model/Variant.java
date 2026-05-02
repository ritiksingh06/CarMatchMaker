package com.example.carmatchmaker.model;

import com.example.carmatchmaker.enums.FuelType;
import com.example.carmatchmaker.enums.Transmission;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "variants")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Variant {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_id", nullable = false)
    private Car car;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private Double price; // in lakhs
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Transmission transmission;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FuelType fuelType;
    
    @Column(length = 500)
    private String keyFeatures;
    
    public String getPriceFormatted() {
        return String.format("₹%.2f Lakh", price);
    }
}
