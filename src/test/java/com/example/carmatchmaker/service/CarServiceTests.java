package com.example.carmatchmaker.service;

import com.example.carmatchmaker.dto.CarFilterRequest;
import com.example.carmatchmaker.enums.BodyType;
import com.example.carmatchmaker.enums.FuelType;
import com.example.carmatchmaker.enums.Transmission;
import com.example.carmatchmaker.model.Car;
import com.example.carmatchmaker.repository.CarRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarServiceTests {

    @Mock
    private CarRepository carRepository;

    @InjectMocks
    private CarService carService;

    private Car swift;
    private Car creta;
    private Car nexon;

    @BeforeEach
    void setUp() {
        swift = Car.builder()
                .id(1L).make("Maruti").model("Swift")
                .bodyType(BodyType.HATCHBACK).fuelType(FuelType.PETROL)
                .transmission(Transmission.MANUAL)
                .priceMin(6.5).priceMax(9.5)
                .mileage(23.0).safetyRating(3.0).userRating(4.3)
                .bootSpace(268).seats(5).engine("1.2L")
                .features("Touchscreen").pros("Efficient").cons("Basic")
                .build();

        creta = Car.builder()
                .id(2L).make("Hyundai").model("Creta")
                .bodyType(BodyType.SUV).fuelType(FuelType.DIESEL)
                .transmission(Transmission.AUTOMATIC)
                .priceMin(11.0).priceMax(20.0)
                .mileage(17.0).safetyRating(5.0).userRating(4.5)
                .bootSpace(433).seats(5).engine("1.5L Turbo")
                .features("Sunroof, ADAS").pros("Feature rich").cons("Expensive")
                .build();

        nexon = Car.builder()
                .id(3L).make("Tata").model("Nexon")
                .bodyType(BodyType.COMPACT_SUV).fuelType(FuelType.PETROL)
                .transmission(Transmission.MANUAL)
                .priceMin(8.0).priceMax(14.0)
                .mileage(17.4).safetyRating(5.0).userRating(4.2)
                .bootSpace(350).seats(5).engine("1.2L Turbo")
                .features("Connected car").pros("Safe").cons("Noisy engine")
                .build();
    }

    @Nested
    @DisplayName("getAllCars")
    class GetAllCars {

        @Test
        @DisplayName("should return all cars from repository")
        void returnsAllCars() {
            when(carRepository.findAll()).thenReturn(List.of(swift, creta, nexon));

            List<Car> result = carService.getAllCars();

            assertThat(result).hasSize(3);
            verify(carRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("should return empty list when no cars exist")
        void returnsEmptyWhenNoCars() {
            when(carRepository.findAll()).thenReturn(List.of());

            List<Car> result = carService.getAllCars();

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("getCarById")
    class GetCarById {

        @Test
        @DisplayName("should return car when found")
        void returnsCarWhenFound() {
            when(carRepository.findById(1L)).thenReturn(Optional.of(swift));

            Car result = carService.getCarById(1L);

            assertThat(result).isEqualTo(swift);
            assertThat(result.getMake()).isEqualTo("Maruti");
        }

        @Test
        @DisplayName("should throw RuntimeException when car not found")
        void throwsWhenNotFound() {
            when(carRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> carService.getCarById(99L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Car not found with id: 99");
        }
    }

    @Nested
    @DisplayName("getCarsByBudget")
    class GetCarsByBudget {

        @Test
        @DisplayName("should return cars within budget range")
        void returnsCarsInBudget() {
            when(carRepository.findByPriceMinLessThanEqualAndPriceMaxGreaterThanEqual(10.0, 5.0))
                    .thenReturn(List.of(swift));

            List<Car> result = carService.getCarsByBudget(5.0, 10.0);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getMake()).isEqualTo("Maruti");
        }

        @Test
        @DisplayName("should return empty when no cars in budget")
        void returnsEmptyWhenNoCarsInBudget() {
            when(carRepository.findByPriceMinLessThanEqualAndPriceMaxGreaterThanEqual(3.0, 2.0))
                    .thenReturn(List.of());

            List<Car> result = carService.getCarsByBudget(2.0, 3.0);

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("filterCars")
    class FilterCars {

        @Test
        @DisplayName("should filter with all null parameters and return all cars")
        void filterWithAllNulls() {
            CarFilterRequest filter = new CarFilterRequest();
            when(carRepository.findByFilters(null, null, null, null, null, null, null))
                    .thenReturn(List.of(swift, creta, nexon));

            List<Car> result = carService.filterCars(filter);

            assertThat(result).hasSize(3);
        }

        @Test
        @DisplayName("should normalize zero values to null")
        void normalizeZeroValues() {
            CarFilterRequest filter = CarFilterRequest.builder()
                    .minPrice(0.0)
                    .maxPrice(0.0)
                    .minSafetyRating(0.0)
                    .make("")
                    .build();

            when(carRepository.findByFilters(null, null, null, null, null, null, null))
                    .thenReturn(List.of(swift));

            List<Car> result = carService.filterCars(filter);

            assertThat(result).hasSize(1);
            verify(carRepository).findByFilters(null, null, null, null, null, null, null);
        }

        @Test
        @DisplayName("should filter by price range")
        void filterByPrice() {
            CarFilterRequest filter = CarFilterRequest.builder()
                    .minPrice(5.0)
                    .maxPrice(10.0)
                    .build();

            when(carRepository.findByFilters(5.0, 10.0, null, null, null, null, null))
                    .thenReturn(List.of(swift));

            List<Car> result = carService.filterCars(filter);

            assertThat(result).hasSize(1);
            assertThat(result.get(0)).isEqualTo(swift);
        }

        @Test
        @DisplayName("should sort by price ascending")
        void sortByPrice() {
            CarFilterRequest filter = CarFilterRequest.builder().sortBy("price").build();
            when(carRepository.findByFilters(null, null, null, null, null, null, null))
                    .thenReturn(List.of(creta, swift, nexon));

            List<Car> result = carService.filterCars(filter);

            assertThat(result.get(0).getPriceMin()).isEqualTo(6.5);
            assertThat(result.get(1).getPriceMin()).isEqualTo(8.0);
            assertThat(result.get(2).getPriceMin()).isEqualTo(11.0);
        }

        @Test
        @DisplayName("should sort by mileage descending")
        void sortByMileage() {
            CarFilterRequest filter = CarFilterRequest.builder().sortBy("mileage").build();
            when(carRepository.findByFilters(null, null, null, null, null, null, null))
                    .thenReturn(List.of(creta, swift, nexon));

            List<Car> result = carService.filterCars(filter);

            assertThat(result.get(0).getMileage()).isEqualTo(23.0);
            assertThat(result.get(1).getMileage()).isEqualTo(17.4);
            assertThat(result.get(2).getMileage()).isEqualTo(17.0);
        }

        @Test
        @DisplayName("should sort by safety descending")
        void sortBySafety() {
            CarFilterRequest filter = CarFilterRequest.builder().sortBy("safety").build();
            when(carRepository.findByFilters(null, null, null, null, null, null, null))
                    .thenReturn(List.of(swift, creta, nexon));

            List<Car> result = carService.filterCars(filter);

            assertThat(result.get(0).getSafetyRating()).isEqualTo(5.0);
            assertThat(result.get(2).getSafetyRating()).isEqualTo(3.0);
        }

        @Test
        @DisplayName("should sort by user rating descending")
        void sortByRating() {
            CarFilterRequest filter = CarFilterRequest.builder().sortBy("rating").build();
            when(carRepository.findByFilters(null, null, null, null, null, null, null))
                    .thenReturn(List.of(swift, creta, nexon));

            List<Car> result = carService.filterCars(filter);

            assertThat(result.get(0).getUserRating()).isEqualTo(4.5);
            assertThat(result.get(2).getUserRating()).isEqualTo(4.2);
        }

        @Test
        @DisplayName("should return unsorted list for unknown sort key")
        void sortByUnknownKey() {
            CarFilterRequest filter = CarFilterRequest.builder().sortBy("unknown").build();
            when(carRepository.findByFilters(null, null, null, null, null, null, null))
                    .thenReturn(List.of(creta, swift, nexon));

            List<Car> result = carService.filterCars(filter);

            // Default order preserved
            assertThat(result).containsExactly(creta, swift, nexon);
        }

        @Test
        @DisplayName("should filter by body type")
        void filterByBodyType() {
            CarFilterRequest filter = CarFilterRequest.builder()
                    .bodyType(BodyType.SUV).build();

            when(carRepository.findByFilters(null, null, null, BodyType.SUV, null, null, null))
                    .thenReturn(List.of(creta));

            List<Car> result = carService.filterCars(filter);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getBodyType()).isEqualTo(BodyType.SUV);
        }

        @Test
        @DisplayName("should filter by fuel type")
        void filterByFuelType() {
            CarFilterRequest filter = CarFilterRequest.builder()
                    .fuelType(FuelType.DIESEL).build();

            when(carRepository.findByFilters(null, null, null, null, FuelType.DIESEL, null, null))
                    .thenReturn(List.of(creta));

            List<Car> result = carService.filterCars(filter);

            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("should handle negative prices by normalizing to null")
        void normalizeNegativePrices() {
            CarFilterRequest filter = CarFilterRequest.builder()
                    .minPrice(-5.0)
                    .maxPrice(-10.0)
                    .build();

            when(carRepository.findByFilters(null, null, null, null, null, null, null))
                    .thenReturn(List.of(swift));

            List<Car> result = carService.filterCars(filter);

            verify(carRepository).findByFilters(null, null, null, null, null, null, null);
            assertThat(result).hasSize(1);
        }
    }

    @Nested
    @DisplayName("getAllMakes")
    class GetAllMakes {

        @Test
        @DisplayName("should return distinct makes")
        void returnsDistinctMakes() {
            when(carRepository.findDistinctMakes()).thenReturn(List.of("Hyundai", "Maruti", "Tata"));

            List<String> result = carService.getAllMakes();

            assertThat(result).hasSize(3).containsExactly("Hyundai", "Maruti", "Tata");
        }

        @Test
        @DisplayName("should return empty list when no makes")
        void returnsEmptyMakes() {
            when(carRepository.findDistinctMakes()).thenReturn(List.of());

            List<String> result = carService.getAllMakes();

            assertThat(result).isEmpty();
        }
    }
}
