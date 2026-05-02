package com.example.carmatchmaker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CarMatchmakerApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(CarMatchmakerApplication.class, args);
        System.out.println("\n========================================");
        System.out.println("🚗 Car Matchmaker is running!");
        System.out.println("========================================");
        System.out.println("Access the application at: http://localhost:8080");
        System.out.println("H2 Console (dev only): http://localhost:8080/h2-console");
        System.out.println("Health Check: http://localhost:8080/actuator/health");
        System.out.println("========================================\n");
    }
}
