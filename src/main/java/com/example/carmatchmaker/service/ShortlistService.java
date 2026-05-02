package com.example.carmatchmaker.service;

import com.example.carmatchmaker.model.Car;
import com.example.carmatchmaker.model.ShortlistItem;
import com.example.carmatchmaker.repository.CarRepository;
import com.example.carmatchmaker.repository.ShortlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ShortlistService {
    
    private final ShortlistRepository shortlistRepository;
    private final CarRepository carRepository;
    
    public List<ShortlistItem> getAllShortlistItems() {
        return shortlistRepository.findAllByOrderByCreatedAtDesc();
    }
    
    public boolean isCarInShortlist(Long carId) {
        return shortlistRepository.existsByCarId(carId);
    }
    
    public ShortlistItem addToShortlist(Long carId) {
        // Check if already in shortlist
        if (shortlistRepository.existsByCarId(carId)) {
            return shortlistRepository.findByCarId(carId).orElse(null);
        }
        
        Car car = carRepository.findById(carId)
            .orElseThrow(() -> new RuntimeException("Car not found with id: " + carId));
        
        ShortlistItem item = ShortlistItem.builder()
            .car(car)
            .build();
        
        return shortlistRepository.save(item);
    }
    
    public void removeFromShortlist(Long carId) {
        shortlistRepository.deleteByCarId(carId);
    }
    
    public List<Car> getShortlistedCars() {
        return shortlistRepository.findAllByOrderByCreatedAtDesc()
            .stream()
            .map(ShortlistItem::getCar)
            .toList();
    }
}
