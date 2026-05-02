package com.example.carmatchmaker.repository;

import com.example.carmatchmaker.enums.BodyType;
import com.example.carmatchmaker.enums.FuelType;
import com.example.carmatchmaker.enums.Transmission;
import com.example.carmatchmaker.model.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {
    
    List<Car> findByPriceMinLessThanEqualAndPriceMaxGreaterThanEqual(Double maxBudget, Double minBudget);
    
    /**
     * Filter cars with multiple criteria.
     * Price filtering logic: Shows cars that have ANY variant within the specified price range.
     * - A car with price range [6, 10] will show for budget [5, 15] (overlap exists)
     * - A car with price range [6, 10] will show for budget [8, 12] (partial overlap)
     * - A car with price range [6, 10] will NOT show for budget [11, 15] (no overlap)
     * - A car with price range [6, 10] will NOT show for budget [2, 5] (no overlap)
     */
    @Query("SELECT DISTINCT c FROM Car c WHERE " +
           "(:minPrice IS NULL OR :minPrice = 0 OR c.priceMax >= :minPrice) AND " +
           "(:maxPrice IS NULL OR :maxPrice = 0 OR c.priceMin <= :maxPrice) AND " +
           "(:make IS NULL OR :make = '' OR c.make = :make) AND " +
           "(:bodyType IS NULL OR c.bodyType = :bodyType) AND " +
           "(:fuelType IS NULL OR c.fuelType = :fuelType) AND " +
           "(:transmission IS NULL OR c.transmission = :transmission) AND " +
           "(:minSafetyRating IS NULL OR :minSafetyRating = 0 OR c.safetyRating >= :minSafetyRating)")
    List<Car> findByFilters(
        @Param("minPrice") Double minPrice,
        @Param("maxPrice") Double maxPrice,
        @Param("make") String make,
        @Param("bodyType") BodyType bodyType,
        @Param("fuelType") FuelType fuelType,
        @Param("transmission") Transmission transmission,
        @Param("minSafetyRating") Double minSafetyRating
    );
    
    @Query("SELECT DISTINCT c.make FROM Car c ORDER BY c.make")
    List<String> findDistinctMakes();
}
