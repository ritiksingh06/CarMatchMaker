package com.example.carmatchmaker.service;

import com.example.carmatchmaker.dto.CarFilterRequest;
import com.example.carmatchmaker.model.Car;
import com.example.carmatchmaker.repository.CarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CarService {
    
    private final CarRepository carRepository;
    
    public List<Car> getAllCars() {
        return carRepository.findAll();
    }
    
    public Car getCarById(Long id) {
        return carRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Car not found with id: " + id));
    }
    
    public List<Car> getCarsByBudget(Double minBudget, Double maxBudget) {
        return carRepository.findByPriceMinLessThanEqualAndPriceMaxGreaterThanEqual(maxBudget, minBudget);
    }
    
    public List<Car> filterCars(CarFilterRequest filter) {
        // Normalize filter values (convert empty/zero to null)
        filter.normalize();
        
        List<Car> cars = carRepository.findByFilters(
            filter.getMinPrice(),
            filter.getMaxPrice(),
            filter.getMake(),
            filter.getBodyType(),
            filter.getFuelType(),
            filter.getTransmission(),
            filter.getMinSafetyRating()
        );
        
        // Apply sorting
        if (filter.getSortBy() != null) {
            cars = sortCars(cars, filter.getSortBy());
        }
        
        return cars;
    }
    
    public List<String> getAllMakes() {
        return carRepository.findDistinctMakes();
    }
    
    private List<Car> sortCars(List<Car> cars, String sortBy) {
        return switch (sortBy.toLowerCase()) {
            case "price" -> cars.stream()
                .sorted(Comparator.comparing(Car::getPriceMin))
                .toList();
            case "mileage" -> cars.stream()
                .sorted(Comparator.comparing(Car::getMileage).reversed())
                .toList();
            case "safety" -> cars.stream()
                .sorted(Comparator.comparing(Car::getSafetyRating).reversed())
                .toList();
            case "rating" -> cars.stream()
                .sorted(Comparator.comparing(Car::getUserRating).reversed())
                .toList();
            default -> cars;
        };
    }
}
