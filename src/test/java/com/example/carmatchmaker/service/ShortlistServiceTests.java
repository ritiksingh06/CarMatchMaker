package com.example.carmatchmaker.service;

import com.example.carmatchmaker.enums.BodyType;
import com.example.carmatchmaker.enums.FuelType;
import com.example.carmatchmaker.enums.Transmission;
import com.example.carmatchmaker.model.Car;
import com.example.carmatchmaker.model.ShortlistItem;
import com.example.carmatchmaker.repository.CarRepository;
import com.example.carmatchmaker.repository.ShortlistRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShortlistServiceTests {

    @Mock
    private ShortlistRepository shortlistRepository;

    @Mock
    private CarRepository carRepository;

    @InjectMocks
    private ShortlistService shortlistService;

    private Car testCar;
    private ShortlistItem testItem;

    @BeforeEach
    void setUp() {
        testCar = Car.builder()
                .id(1L).make("Maruti").model("Swift")
                .bodyType(BodyType.HATCHBACK).fuelType(FuelType.PETROL)
                .transmission(Transmission.MANUAL)
                .priceMin(6.5).priceMax(9.5)
                .mileage(23.0).safetyRating(3.0).userRating(4.3)
                .bootSpace(268).seats(5).engine("1.2L")
                .features("Touchscreen").pros("Good").cons("Basic")
                .build();

        testItem = ShortlistItem.builder()
                .id(1L)
                .car(testCar)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Nested
    @DisplayName("getAllShortlistItems")
    class GetAllShortlistItems {

        @Test
        @DisplayName("should return items ordered by creation time descending")
        void returnsItemsOrdered() {
            when(shortlistRepository.findAllByOrderByCreatedAtDesc())
                    .thenReturn(List.of(testItem));

            List<ShortlistItem> result = shortlistService.getAllShortlistItems();

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getCar().getMake()).isEqualTo("Maruti");
        }

        @Test
        @DisplayName("should return empty list when shortlist is empty")
        void returnsEmptyList() {
            when(shortlistRepository.findAllByOrderByCreatedAtDesc())
                    .thenReturn(List.of());

            List<ShortlistItem> result = shortlistService.getAllShortlistItems();

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("isCarInShortlist")
    class IsCarInShortlist {

        @Test
        @DisplayName("should return true when car is shortlisted")
        void returnsTrueWhenShortlisted() {
            when(shortlistRepository.existsByCarId(1L)).thenReturn(true);

            boolean result = shortlistService.isCarInShortlist(1L);

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("should return false when car is not shortlisted")
        void returnsFalseWhenNotShortlisted() {
            when(shortlistRepository.existsByCarId(99L)).thenReturn(false);

            boolean result = shortlistService.isCarInShortlist(99L);

            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("addToShortlist")
    class AddToShortlist {

        @Test
        @DisplayName("should add car to shortlist when not already present")
        void addsCarWhenNotPresent() {
            when(shortlistRepository.existsByCarId(1L)).thenReturn(false);
            when(carRepository.findById(1L)).thenReturn(Optional.of(testCar));
            when(shortlistRepository.save(any(ShortlistItem.class))).thenReturn(testItem);

            ShortlistItem result = shortlistService.addToShortlist(1L);

            assertThat(result).isNotNull();
            assertThat(result.getCar()).isEqualTo(testCar);
            verify(shortlistRepository).save(any(ShortlistItem.class));
        }

        @Test
        @DisplayName("should return existing item when car already shortlisted")
        void returnsExistingWhenAlreadyPresent() {
            when(shortlistRepository.existsByCarId(1L)).thenReturn(true);
            when(shortlistRepository.findByCarId(1L)).thenReturn(Optional.of(testItem));

            ShortlistItem result = shortlistService.addToShortlist(1L);

            assertThat(result).isEqualTo(testItem);
            verify(shortlistRepository, never()).save(any());
        }

        @Test
        @DisplayName("should return null when car already shortlisted but not found")
        void returnsNullWhenAlreadyPresentButNotFound() {
            when(shortlistRepository.existsByCarId(1L)).thenReturn(true);
            when(shortlistRepository.findByCarId(1L)).thenReturn(Optional.empty());

            ShortlistItem result = shortlistService.addToShortlist(1L);

            assertThat(result).isNull();
            verify(shortlistRepository, never()).save(any());
        }

        @Test
        @DisplayName("should throw RuntimeException when car ID does not exist")
        void throwsWhenCarNotFound() {
            when(shortlistRepository.existsByCarId(99L)).thenReturn(false);
            when(carRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> shortlistService.addToShortlist(99L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Car not found with id: 99");
        }
    }

    @Nested
    @DisplayName("removeFromShortlist")
    class RemoveFromShortlist {

        @Test
        @DisplayName("should delete shortlist item by car ID")
        void removesSuccessfully() {
            doNothing().when(shortlistRepository).deleteByCarId(1L);

            shortlistService.removeFromShortlist(1L);

            verify(shortlistRepository).deleteByCarId(1L);
        }

        @Test
        @DisplayName("should not throw when car is not in shortlist")
        void noOpWhenNotInShortlist() {
            doNothing().when(shortlistRepository).deleteByCarId(99L);

            shortlistService.removeFromShortlist(99L);

            verify(shortlistRepository).deleteByCarId(99L);
        }
    }

    @Nested
    @DisplayName("getShortlistedCars")
    class GetShortlistedCars {

        @Test
        @DisplayName("should return list of cars from shortlist items")
        void returnsCarsFromItems() {
            Car car2 = Car.builder().id(2L).make("Tata").model("Nexon")
                    .bodyType(BodyType.COMPACT_SUV).fuelType(FuelType.PETROL)
                    .transmission(Transmission.MANUAL).priceMin(8.0).priceMax(14.0)
                    .mileage(17.4).safetyRating(5.0).userRating(4.2)
                    .bootSpace(350).seats(5).engine("1.2L")
                    .features("").pros("").cons("")
                    .build();

            ShortlistItem item2 = ShortlistItem.builder()
                    .id(2L).car(car2).createdAt(LocalDateTime.now()).build();

            when(shortlistRepository.findAllByOrderByCreatedAtDesc())
                    .thenReturn(List.of(testItem, item2));

            List<Car> result = shortlistService.getShortlistedCars();

            assertThat(result).hasSize(2);
            assertThat(result.get(0).getMake()).isEqualTo("Maruti");
            assertThat(result.get(1).getMake()).isEqualTo("Tata");
        }

        @Test
        @DisplayName("should return empty list when no cars shortlisted")
        void returnsEmptyWhenNoShortlist() {
            when(shortlistRepository.findAllByOrderByCreatedAtDesc())
                    .thenReturn(List.of());

            List<Car> result = shortlistService.getShortlistedCars();

            assertThat(result).isEmpty();
        }
    }
}
