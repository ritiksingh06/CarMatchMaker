package com.example.carmatchmaker.dto;

import com.example.carmatchmaker.enums.BodyType;
import com.example.carmatchmaker.enums.FuelType;
import com.example.carmatchmaker.enums.Transmission;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CarFilterRequestTests {

    @Nested
    @DisplayName("normalize()")
    class Normalize {

        @Test
        @DisplayName("should set zero minPrice to null")
        void zeroMinPriceBecomesNull() {
            CarFilterRequest filter = CarFilterRequest.builder().minPrice(0.0).build();
            filter.normalize();
            assertThat(filter.getMinPrice()).isNull();
        }

        @Test
        @DisplayName("should set negative minPrice to null")
        void negativeMinPriceBecomesNull() {
            CarFilterRequest filter = CarFilterRequest.builder().minPrice(-5.0).build();
            filter.normalize();
            assertThat(filter.getMinPrice()).isNull();
        }

        @Test
        @DisplayName("should keep positive minPrice unchanged")
        void positiveMinPriceUnchanged() {
            CarFilterRequest filter = CarFilterRequest.builder().minPrice(5.0).build();
            filter.normalize();
            assertThat(filter.getMinPrice()).isEqualTo(5.0);
        }

        @Test
        @DisplayName("should set zero maxPrice to null")
        void zeroMaxPriceBecomesNull() {
            CarFilterRequest filter = CarFilterRequest.builder().maxPrice(0.0).build();
            filter.normalize();
            assertThat(filter.getMaxPrice()).isNull();
        }

        @Test
        @DisplayName("should set negative maxPrice to null")
        void negativeMaxPriceBecomesNull() {
            CarFilterRequest filter = CarFilterRequest.builder().maxPrice(-10.0).build();
            filter.normalize();
            assertThat(filter.getMaxPrice()).isNull();
        }

        @Test
        @DisplayName("should keep positive maxPrice unchanged")
        void positiveMaxPriceUnchanged() {
            CarFilterRequest filter = CarFilterRequest.builder().maxPrice(20.0).build();
            filter.normalize();
            assertThat(filter.getMaxPrice()).isEqualTo(20.0);
        }

        @Test
        @DisplayName("should set empty make to null")
        void emptyMakeBecomesNull() {
            CarFilterRequest filter = CarFilterRequest.builder().make("").build();
            filter.normalize();
            assertThat(filter.getMake()).isNull();
        }

        @Test
        @DisplayName("should set whitespace-only make to null")
        void whitespaceMakeBecomesNull() {
            CarFilterRequest filter = CarFilterRequest.builder().make("   ").build();
            filter.normalize();
            assertThat(filter.getMake()).isNull();
        }

        @Test
        @DisplayName("should keep valid make unchanged")
        void validMakeUnchanged() {
            CarFilterRequest filter = CarFilterRequest.builder().make("Maruti").build();
            filter.normalize();
            assertThat(filter.getMake()).isEqualTo("Maruti");
        }

        @Test
        @DisplayName("should set zero minSafetyRating to null")
        void zeroSafetyBecomesNull() {
            CarFilterRequest filter = CarFilterRequest.builder().minSafetyRating(0.0).build();
            filter.normalize();
            assertThat(filter.getMinSafetyRating()).isNull();
        }

        @Test
        @DisplayName("should set negative minSafetyRating to null")
        void negativeSafetyBecomesNull() {
            CarFilterRequest filter = CarFilterRequest.builder().minSafetyRating(-1.0).build();
            filter.normalize();
            assertThat(filter.getMinSafetyRating()).isNull();
        }

        @Test
        @DisplayName("should keep positive minSafetyRating unchanged")
        void positiveSafetyUnchanged() {
            CarFilterRequest filter = CarFilterRequest.builder().minSafetyRating(4.0).build();
            filter.normalize();
            assertThat(filter.getMinSafetyRating()).isEqualTo(4.0);
        }

        @Test
        @DisplayName("should handle null fields gracefully")
        void nullFieldsNoOp() {
            CarFilterRequest filter = new CarFilterRequest();
            filter.normalize();
            assertThat(filter.getMinPrice()).isNull();
            assertThat(filter.getMaxPrice()).isNull();
            assertThat(filter.getMake()).isNull();
            assertThat(filter.getMinSafetyRating()).isNull();
        }

        @Test
        @DisplayName("should not touch bodyType, fuelType, transmission, sortBy")
        void doesNotTouchOtherFields() {
            CarFilterRequest filter = CarFilterRequest.builder()
                    .bodyType(BodyType.SUV)
                    .fuelType(FuelType.DIESEL)
                    .transmission(Transmission.AUTOMATIC)
                    .sortBy("price")
                    .build();
            filter.normalize();
            assertThat(filter.getBodyType()).isEqualTo(BodyType.SUV);
            assertThat(filter.getFuelType()).isEqualTo(FuelType.DIESEL);
            assertThat(filter.getTransmission()).isEqualTo(Transmission.AUTOMATIC);
            assertThat(filter.getSortBy()).isEqualTo("price");
        }
    }
}
