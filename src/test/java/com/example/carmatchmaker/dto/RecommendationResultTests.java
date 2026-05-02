package com.example.carmatchmaker.dto;

import com.example.carmatchmaker.model.Car;
import com.example.carmatchmaker.model.Variant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RecommendationResultTests {

    @Nested
    @DisplayName("getScorePercentage()")
    class ScorePercentage {

        @Test
        @DisplayName("should round score to nearest integer")
        void roundsToNearestInt() {
            RecommendationResult result = RecommendationResult.builder().score(85.7).build();
            assertThat(result.getScorePercentage()).isEqualTo(86);
        }

        @Test
        @DisplayName("should round down when decimal < 0.5")
        void roundsDown() {
            RecommendationResult result = RecommendationResult.builder().score(60.3).build();
            assertThat(result.getScorePercentage()).isEqualTo(60);
        }

        @Test
        @DisplayName("should handle 0 score")
        void zeroScore() {
            RecommendationResult result = RecommendationResult.builder().score(0.0).build();
            assertThat(result.getScorePercentage()).isEqualTo(0);
        }

        @Test
        @DisplayName("should handle 100 score")
        void perfectScore() {
            RecommendationResult result = RecommendationResult.builder().score(100.0).build();
            assertThat(result.getScorePercentage()).isEqualTo(100);
        }

        @Test
        @DisplayName("should handle exact 0.5 rounding")
        void halfRounds() {
            RecommendationResult result = RecommendationResult.builder().score(50.5).build();
            assertThat(result.getScorePercentage()).isEqualTo(51);
        }
    }

    @Nested
    @DisplayName("getScoreColor()")
    class ScoreColor {

        @Test
        @DisplayName("should return green for score >= 80")
        void greenForHighScore() {
            assertThat(RecommendationResult.builder().score(80.0).build().getScoreColor()).isEqualTo("green");
            assertThat(RecommendationResult.builder().score(95.0).build().getScoreColor()).isEqualTo("green");
            assertThat(RecommendationResult.builder().score(100.0).build().getScoreColor()).isEqualTo("green");
        }

        @Test
        @DisplayName("should return blue for score >= 60 and < 80")
        void blueForMediumHighScore() {
            assertThat(RecommendationResult.builder().score(60.0).build().getScoreColor()).isEqualTo("blue");
            assertThat(RecommendationResult.builder().score(79.9).build().getScoreColor()).isEqualTo("blue");
        }

        @Test
        @DisplayName("should return yellow for score >= 40 and < 60")
        void yellowForMediumScore() {
            assertThat(RecommendationResult.builder().score(40.0).build().getScoreColor()).isEqualTo("yellow");
            assertThat(RecommendationResult.builder().score(59.9).build().getScoreColor()).isEqualTo("yellow");
        }

        @Test
        @DisplayName("should return red for score < 40")
        void redForLowScore() {
            assertThat(RecommendationResult.builder().score(0.0).build().getScoreColor()).isEqualTo("red");
            assertThat(RecommendationResult.builder().score(39.9).build().getScoreColor()).isEqualTo("red");
            assertThat(RecommendationResult.builder().score(10.0).build().getScoreColor()).isEqualTo("red");
        }

        @Test
        @DisplayName("should handle boundary at exactly 80")
        void boundaryAt80() {
            assertThat(RecommendationResult.builder().score(80.0).build().getScoreColor()).isEqualTo("green");
            assertThat(RecommendationResult.builder().score(79.99).build().getScoreColor()).isEqualTo("blue");
        }

        @Test
        @DisplayName("should handle boundary at exactly 60")
        void boundaryAt60() {
            assertThat(RecommendationResult.builder().score(60.0).build().getScoreColor()).isEqualTo("blue");
            assertThat(RecommendationResult.builder().score(59.99).build().getScoreColor()).isEqualTo("yellow");
        }

        @Test
        @DisplayName("should handle boundary at exactly 40")
        void boundaryAt40() {
            assertThat(RecommendationResult.builder().score(40.0).build().getScoreColor()).isEqualTo("yellow");
            assertThat(RecommendationResult.builder().score(39.99).build().getScoreColor()).isEqualTo("red");
        }
    }
}
