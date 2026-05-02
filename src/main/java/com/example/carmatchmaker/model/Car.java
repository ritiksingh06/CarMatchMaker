package com.example.carmatchmaker.model;

import com.example.carmatchmaker.enums.BodyType;
import com.example.carmatchmaker.enums.FuelType;
import com.example.carmatchmaker.enums.Transmission;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cars")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Car {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String make;
    
    @Column(nullable = false)
    private String model;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BodyType bodyType;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FuelType fuelType;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Transmission transmission;
    
    @Column(nullable = false)
    private Double priceMin;
    
    @Column(nullable = false)
    private Double priceMax;
    
    @Column(nullable = false)
    private Double mileage; // km/l
    
    @Column(nullable = false)
    private Double safetyRating; // 0-5
    
    @Column(nullable = false)
    private Double userRating; // 0-5
    
    private Integer bootSpace; // liters
    
    @Column(nullable = false)
    private Integer seats;
    
    private String engine;
    
    @Column(length = 1000)
    private String features;
    
    @Column(length = 500)
    private String pros;
    
    @Column(length = 500)
    private String cons;
    
    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Variant> variants = new ArrayList<>();
    
    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Review> reviews = new ArrayList<>();
    
    public String getFullName() {
        return make + " " + model;
    }
    
    public String getPriceRange() {
        return String.format("₹%.2f - ₹%.2f Lakh", priceMin, priceMax);
    }
}
