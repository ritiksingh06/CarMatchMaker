package com.example.carmatchmaker.dto;

import com.example.carmatchmaker.model.Car;
import com.example.carmatchmaker.model.Variant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationResult {
    
    private Car car;
    private Double score;
    private List<String> matchReasons;
    private List<String> tradeoffs;
    private Variant bestVariant;
    
    public int getScorePercentage() {
        return (int) Math.round(score);
    }
    
    public String getScoreColor() {
        if (score >= 80) return "green";
        if (score >= 60) return "blue";
        if (score >= 40) return "yellow";
        return "red";
    }
}
