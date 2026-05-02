package com.example.carmatchmaker.config;

import com.example.carmatchmaker.enums.*;
import com.example.carmatchmaker.model.Car;
import com.example.carmatchmaker.model.Review;
import com.example.carmatchmaker.model.Variant;
import com.example.carmatchmaker.repository.CarRepository;
import com.example.carmatchmaker.repository.ReviewRepository;
import com.example.carmatchmaker.repository.VariantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataSeeder {
    
    @Bean
    CommandLineRunner initDatabase(CarRepository carRepository, 
                                   VariantRepository variantRepository,
                                   ReviewRepository reviewRepository) {
        return args -> {
            if (carRepository.count() > 0) {
                log.info("Database already seeded. Skipping...");
                return;
            }
            
            log.info("Seeding database with Indian car data...");
            
            List<Car> cars = createAllCars();
            
            for (Car car : cars) {
                // Set bidirectional relationships before saving
                for (Variant variant : car.getVariants()) {
                    variant.setCar(car);
                }
                
                for (Review review : car.getReviews()) {
                    review.setCar(car);
                }
                
                // Save car with cascade to variants and reviews
                carRepository.save(car);
            }
            
            log.info("Database seeded with {} cars", cars.size());
        };
    }
    
    private List<Car> createAllCars() {
        List<Car> cars = new ArrayList<>();
        
        // 1. Maruti Swift
        cars.add(createCar("Maruti", "Swift", BodyType.HATCHBACK, FuelType.PETROL, 
            Transmission.MANUAL, 6.49, 9.64, 23.2, 3.0, 4.3, 268, 5, "1.2L K-Series",
            "Touchscreen infotainment, Apple CarPlay, Android Auto, Cruise control, Keyless entry",
            "Fuel efficient, Reliable, Low maintenance, Spacious cabin",
            "Average build quality, Basic features in lower variants",
            List.of(
                createVariant("LXi", 6.49, Transmission.MANUAL, FuelType.PETROL, "Basic features, AC, Power steering"),
                createVariant("VXi", 7.46, Transmission.MANUAL, FuelType.PETROL, "Touchscreen, Alloy wheels"),
                createVariant("ZXi", 8.56, Transmission.MANUAL, FuelType.PETROL, "LED projector headlamps, Auto AC"),
                createVariant("ZXi+ AMT", 9.64, Transmission.AUTOMATIC, FuelType.PETROL, "Automatic transmission, Connected features")
            ),
            List.of(
                createReview(4.5, "Best value for money hatchback. Very reliable.", 0.8),
                createReview(4.0, "Great mileage and low maintenance cost.", 0.7)
            )));
        
        // 2. Hyundai i20
        cars.add(createCar("Hyundai", "i20", BodyType.HATCHBACK, FuelType.PETROL,
            Transmission.MANUAL, 7.04, 11.52, 20.5, 3.0, 4.4, 311, 5, "1.2L Kappa",
            "10.25-inch touchscreen, BlueLink, Wireless charging, Sunroof, Bose speakers",
            "Premium interior, Feature loaded, Good looks, Comfortable ride",
            "Higher price, Average mileage, Service costs can be high",
            List.of(
                createVariant("Magna", 7.04, Transmission.MANUAL, FuelType.PETROL, "Basic comfort features"),
                createVariant("Sportz", 8.75, Transmission.MANUAL, FuelType.PETROL, "Touchscreen, Cruise control"),
                createVariant("Asta", 10.14, Transmission.MANUAL, FuelType.PETROL, "Sunroof, Digital cluster"),
                createVariant("Asta(O) DCT", 11.52, Transmission.AUTOMATIC, FuelType.PETROL, "All features, Dual-clutch auto")
            ),
            List.of(
                createReview(4.5, "Excellent features and build quality. Worth the premium.", 0.85),
                createReview(4.3, "Very comfortable and loaded with tech.", 0.75)
            )));
        
        // 3. Tata Punch
        cars.add(createCar("Tata", "Punch", BodyType.COMPACT_SUV, FuelType.PETROL,
            Transmission.MANUAL, 6.00, 10.00, 18.8, 5.0, 4.2, 366, 5, "1.2L Revotron",
            "7-inch touchscreen, Reverse camera, Dual airbags, iRA connected features",
            "High ground clearance, Safe, Affordable, Good practicality",
            "Underpowered engine, Noisy cabin at high speeds",
            List.of(
                createVariant("Pure", 6.00, Transmission.MANUAL, FuelType.PETROL, "Basic safety features"),
                createVariant("Adventure", 7.23, Transmission.MANUAL, FuelType.PETROL, "Touchscreen, Projector lamps"),
                createVariant("Accomplished", 8.23, Transmission.MANUAL, FuelType.PETROL, "Auto AC, Alloys"),
                createVariant("Creative+ AMT", 10.00, Transmission.AUTOMATIC, FuelType.PETROL, "All features, AMT")
            ),
            List.of(
                createReview(4.0, "Best micro SUV in India. Very safe and practical.", 0.8),
                createReview(4.3, "Great value for money. 5-star safety rating is impressive.", 0.85)
            )));
        
        // 4. Tata Nexon
        cars.add(createCar("Tata", "Nexon", BodyType.COMPACT_SUV, FuelType.PETROL,
            Transmission.MANUAL, 7.99, 14.60, 17.4, 5.0, 4.4, 350, 5, "1.2L Turbo Revotron",
            "10.25-inch touchscreen, Wireless charger, Sunroof, JBL speakers, Connected features",
            "5-star safety, Turbocharged power, Feature rich, Spacious",
            "Ride quality can be bouncy, After-sales service varies",
            List.of(
                createVariant("Smart", 7.99, Transmission.MANUAL, FuelType.PETROL, "Dual airbags, Touchscreen"),
                createVariant("Pure+", 9.39, Transmission.MANUAL, FuelType.PETROL, "Cruise control, Rear AC vents"),
                createVariant("Creative+", 11.69, Transmission.MANUAL, FuelType.PETROL, "Sunroof, Wireless charging"),
                createVariant("Fearless+ DCA", 14.60, Transmission.AUTOMATIC, FuelType.PETROL, "All features, Dual-clutch auto")
            ),
            List.of(
                createReview(4.5, "Excellent safety and features. Best compact SUV.", 0.9),
                createReview(4.2, "Great turbo engine and premium interiors.", 0.75)
            )));
        
        // 5. Mahindra XUV 3XO
        cars.add(createCar("Mahindra", "XUV 3XO", BodyType.COMPACT_SUV, FuelType.PETROL,
            Transmission.MANUAL, 7.49, 15.49, 18.1, 4.0, 4.3, 364, 5, "1.2L TGDi Turbo",
            "10.25-inch dual screens, Panoramic sunroof, Wireless charging, ADAS Level 2",
            "Feature loaded, Powerful engine, Good build, Premium feel",
            "Steep pricing for top variants, Fuel economy could be better",
            List.of(
                createVariant("MX1", 7.49, Transmission.MANUAL, FuelType.PETROL, "Basic features"),
                createVariant("MX2", 9.19, Transmission.MANUAL, FuelType.PETROL, "Touchscreen, Cruise control"),
                createVariant("AX5", 12.49, Transmission.MANUAL, FuelType.PETROL, "Sunroof, Digital cluster"),
                createVariant("AX7 AT", 15.49, Transmission.AUTOMATIC, FuelType.PETROL, "ADAS, All features")
            ),
            List.of(
                createReview(4.4, "Loaded with features. Best in class tech.", 0.85),
                createReview(4.2, "Powerful engine and great safety features.", 0.8)
            )));
        
        // 6. Hyundai Creta
        cars.add(createCar("Hyundai", "Creta", BodyType.SUV, FuelType.PETROL,
            Transmission.MANUAL, 11.00, 20.15, 17.4, 3.0, 4.5, 433, 5, "1.5L MPi",
            "10.25-inch dual screens, Panoramic sunroof, ADAS Level 2, Ventilated seats, Bose audio",
            "Premium build, Feature rich, Comfortable, Strong brand value",
            "Expensive, Mileage could be better, Turbo variant needed",
            List.of(
                createVariant("EX", 11.00, Transmission.MANUAL, FuelType.PETROL, "Basic comfort features"),
                createVariant("S", 14.51, Transmission.MANUAL, FuelType.PETROL, "Touchscreen, Cruise control"),
                createVariant("SX(O)", 18.24, Transmission.MANUAL, FuelType.PETROL, "Sunroof, Ventilated seats"),
                createVariant("SX(O) DCT", 20.15, Transmission.AUTOMATIC, FuelType.PETROL, "All features, DCT")
            ),
            List.of(
                createReview(4.6, "Best SUV in segment. Very reliable and feature packed.", 0.9),
                createReview(4.4, "Excellent build quality and comfort.", 0.85)
            )));
        
        // 7. Kia Seltos
        cars.add(createCar("Kia", "Seltos", BodyType.SUV, FuelType.PETROL,
            Transmission.MANUAL, 10.90, 20.35, 16.8, 3.0, 4.5, 433, 5, "1.5L MPi",
            "10.25-inch dual screens, 360-degree camera, Ventilated seats, Bose audio, Connected tech",
            "Bold design, Feature loaded, Multiple engine options, Comfortable",
            "Ride can be stiff, Higher maintenance costs",
            List.of(
                createVariant("HTE", 10.90, Transmission.MANUAL, FuelType.PETROL, "Basic features"),
                createVariant("HTK", 13.40, Transmission.MANUAL, FuelType.PETROL, "Touchscreen, Alloys"),
                createVariant("GTX", 17.90, Transmission.MANUAL, FuelType.PETROL, "Sunroof, Digital cluster"),
                createVariant("GTX+ DCT", 20.35, Transmission.AUTOMATIC, FuelType.PETROL, "All features, DCT")
            ),
            List.of(
                createReview(4.5, "Great features and strong engine options.", 0.88),
                createReview(4.3, "Stylish and loaded with tech.", 0.82)
            )));
        
        // 8. Honda City
        cars.add(createCar("Honda", "City", BodyType.SEDAN, FuelType.PETROL,
            Transmission.MANUAL, 11.82, 16.35, 17.8, 4.0, 4.6, 506, 5, "1.5L i-VTEC",
            "8-inch touchscreen, Honda Connect, Sunroof, Cruise control, Alexa compatibility",
            "Refined engine, Spacious cabin, Large boot, Reliable, Premium feel",
            "Higher price, Features could be better, Not the most exciting to drive",
            List.of(
                createVariant("V", 11.82, Transmission.MANUAL, FuelType.PETROL, "Basic features"),
                createVariant("VX", 13.51, Transmission.MANUAL, FuelType.PETROL, "Touchscreen, Cruise control"),
                createVariant("ZX", 15.28, Transmission.MANUAL, FuelType.PETROL, "Sunroof, Connected features"),
                createVariant("ZX CVT", 16.35, Transmission.AUTOMATIC, FuelType.PETROL, "All features, CVT")
            ),
            List.of(
                createReview(4.7, "Best sedan in the segment. Very refined and reliable.", 0.92),
                createReview(4.5, "Spacious and comfortable. Great build quality.", 0.88)
            )));
        
        // 9. Skoda Slavia
        cars.add(createCar("Skoda", "Slavia", BodyType.SEDAN, FuelType.PETROL,
            Transmission.MANUAL, 11.39, 18.89, 18.1, 4.0, 4.4, 521, 5, "1.0L TSI",
            "10-inch touchscreen, Wireless charging, Ventilated seats, Connected features, Digital cluster",
            "Fun to drive, Refined engine, Premium build, Spacious, Large boot",
            "Higher maintenance, Service network limited, Expensive variants",
            List.of(
                createVariant("Active", 11.39, Transmission.MANUAL, FuelType.PETROL, "Basic safety features"),
                createVariant("Ambition", 13.29, Transmission.MANUAL, FuelType.PETROL, "Touchscreen, Cruise control"),
                createVariant("Style", 16.39, Transmission.MANUAL, FuelType.PETROL, "Sunroof, Ventilated seats"),
                createVariant("Style AT", 18.89, Transmission.AUTOMATIC, FuelType.PETROL, "All features, DSG")
            ),
            List.of(
                createReview(4.5, "Best driving dynamics in segment. Feels premium.", 0.87),
                createReview(4.3, "Great build quality and powerful engine.", 0.83)
            )));
        
        // 10. Volkswagen Virtus
        cars.add(createCar("Volkswagen", "Virtus", BodyType.SEDAN, FuelType.PETROL,
            Transmission.MANUAL, 11.56, 19.11, 18.2, 4.0, 4.4, 521, 5, "1.0L TSI",
            "10-inch touchscreen, Digital cockpit, Wireless charging, Ventilated seats, Sunroof",
            "Solid build, Premium feel, Fun to drive, Spacious, Feature rich",
            "Higher maintenance costs, Limited service network, Expensive",
            List.of(
                createVariant("Comfortline", 11.56, Transmission.MANUAL, FuelType.PETROL, "Basic features"),
                createVariant("Highline", 14.01, Transmission.MANUAL, FuelType.PETROL, "Touchscreen, Cruise control"),
                createVariant("Topline", 17.36, Transmission.MANUAL, FuelType.PETROL, "Sunroof, All features"),
                createVariant("GT DSG", 19.11, Transmission.AUTOMATIC, FuelType.PETROL, "1.5L TSI, Performance tuned")
            ),
            List.of(
                createReview(4.5, "Excellent build quality and driving experience.", 0.88),
                createReview(4.4, "Premium sedan with great features.", 0.85)
            )));
        
        // 11. Toyota Hyryder
        cars.add(createCar("Toyota", "Urban Cruiser Hyryder", BodyType.SUV, FuelType.HYBRID,
            Transmission.AUTOMATIC, 10.48, 19.99, 27.9, 3.0, 4.3, 373, 5, "1.5L Hybrid",
            "9-inch touchscreen, 360-degree camera, Ventilated seats, Panoramic sunroof, HUD",
            "Excellent mileage, Hybrid tech, Toyota reliability, Feature rich",
            "Higher initial cost, CVT can feel sluggish, Limited service network",
            List.of(
                createVariant("E", 10.48, Transmission.MANUAL, FuelType.PETROL, "Basic features"),
                createVariant("S Hybrid", 15.88, Transmission.AUTOMATIC, FuelType.HYBRID, "Hybrid tech, Touchscreen"),
                createVariant("G Hybrid", 17.76, Transmission.AUTOMATIC, FuelType.HYBRID, "Sunroof, Connected features"),
                createVariant("V Hybrid", 19.99, Transmission.AUTOMATIC, FuelType.HYBRID, "All features, Panoramic sunroof")
            ),
            List.of(
                createReview(4.4, "Best hybrid SUV. Amazing mileage and Toyota reliability.", 0.87),
                createReview(4.2, "Feature loaded and very efficient.", 0.82)
            )));
        
        // 12. Maruti Grand Vitara
        cars.add(createCar("Maruti", "Grand Vitara", BodyType.SUV, FuelType.HYBRID,
            Transmission.AUTOMATIC, 10.70, 19.95, 27.9, 3.0, 4.3, 373, 5, "1.5L Hybrid",
            "9-inch SmartPlay Pro, 360-degree camera, Ventilated seats, Panoramic sunroof, HUD",
            "Strong hybrid tech, Good mileage, Premium interior, Feature loaded",
            "Higher price, Average build quality compared to competition",
            List.of(
                createVariant("Sigma", 10.70, Transmission.MANUAL, FuelType.PETROL, "Basic features"),
                createVariant("Zeta+ Hybrid", 16.29, Transmission.AUTOMATIC, FuelType.HYBRID, "Hybrid tech, Touchscreen"),
                createVariant("Alpha+ Hybrid", 18.65, Transmission.AUTOMATIC, FuelType.HYBRID, "Sunroof, Connected features"),
                createVariant("Alpha+ Hybrid AWD", 19.95, Transmission.AUTOMATIC, FuelType.HYBRID, "All features, AWD")
            ),
            List.of(
                createReview(4.3, "Great mileage and features. Good value hybrid.", 0.85),
                createReview(4.1, "Comfortable and efficient.", 0.78)
            )));
        
        // 13. Mahindra Scorpio N
        cars.add(createCar("Mahindra", "Scorpio N", BodyType.SUV, FuelType.DIESEL,
            Transmission.MANUAL, 13.60, 24.54, 15.2, 3.0, 4.4, 373, 7, "2.0L mHawk",
            "8-inch touchscreen, Wireless charging, Sunroof, Connected features, Multiple drive modes",
            "Powerful diesel engine, Comfortable, 7-seater, Muscular design, Off-road capable",
            "Bulky size, Lower mileage, Ride can be bumpy",
            List.of(
                createVariant("Z2", 13.60, Transmission.MANUAL, FuelType.DIESEL, "Basic features"),
                createVariant("Z4", 15.45, Transmission.MANUAL, FuelType.DIESEL, "Touchscreen, Cruise control"),
                createVariant("Z8", 19.49, Transmission.MANUAL, FuelType.DIESEL, "Sunroof, Leather seats"),
                createVariant("Z8 L AT 4WD", 24.54, Transmission.AUTOMATIC, FuelType.DIESEL, "All features, 4WD")
            ),
            List.of(
                createReview(4.5, "Best value 7-seater SUV. Powerful and comfortable.", 0.88),
                createReview(4.3, "Great off-road capability and features.", 0.82)
            )));
        
        // 14. Mahindra XUV700
        cars.add(createCar("Mahindra", "XUV700", BodyType.SUV, FuelType.PETROL,
            Transmission.MANUAL, 13.99, 26.99, 13.5, 5.0, 4.6, 657, 7, "2.0L mStallion Turbo",
            "10.25-inch dual screens, ADAS Level 2, Panoramic sunroof, Sony 3D audio, Wireless charging",
            "Feature loaded, Powerful engines, Spacious, Premium build, Best ADAS",
            "Long waiting periods, Service quality varies, Fuel economy",
            List.of(
                createVariant("MX", 13.99, Transmission.MANUAL, FuelType.PETROL, "Basic features"),
                createVariant("AX3", 16.89, Transmission.MANUAL, FuelType.PETROL, "Touchscreen, Cruise control"),
                createVariant("AX5", 20.49, Transmission.MANUAL, FuelType.PETROL, "Sunroof, Digital screens"),
                createVariant("AX7 AT", 26.99, Transmission.AUTOMATIC, FuelType.PETROL, "All features, ADAS")
            ),
            List.of(
                createReview(4.7, "Best SUV under 30 lakhs. Loaded with features and very powerful.", 0.93),
                createReview(4.5, "Excellent value proposition. ADAS is game-changing.", 0.9)
            )));
        
        // 15. Tata Harrier
        cars.add(createCar("Tata", "Harrier", BodyType.SUV, FuelType.DIESEL,
            Transmission.MANUAL, 15.49, 26.44, 14.6, 5.0, 4.5, 425, 7, "2.0L Kryotec",
            "12.3-inch touchscreen, JBL audio, Panoramic sunroof, Ventilated seats, ADAS Level 2",
            "Commanding road presence, Powerful diesel, Feature rich, 5-star safety",
            "Expensive, Lower mileage, Automatic only in top variants",
            List.of(
                createVariant("Smart", 15.49, Transmission.MANUAL, FuelType.DIESEL, "Basic features"),
                createVariant("Pure+", 17.69, Transmission.MANUAL, FuelType.DIESEL, "Touchscreen, Cruise control"),
                createVariant("Adventure+", 21.99, Transmission.MANUAL, FuelType.DIESEL, "Sunroof, Digital cluster"),
                createVariant("Fearless+ AT", 26.44, Transmission.AUTOMATIC, FuelType.DIESEL, "All features, Auto")
            ),
            List.of(
                createReview(4.6, "Best looking SUV. Very safe and feature loaded.", 0.9),
                createReview(4.4, "Great build quality and powerful engine.", 0.87)
            )));
        
        // 16. Tata Safari
        cars.add(createCar("Tata", "Safari", BodyType.SUV, FuelType.DIESEL,
            Transmission.MANUAL, 16.19, 27.34, 14.1, 5.0, 4.5, 447, 7, "2.0L Kryotec",
            "12.3-inch touchscreen, JBL audio, Panoramic sunroof, ADAS Level 2, Captain seats",
            "Premium 7-seater, Powerful diesel, Feature rich, Excellent safety",
            "Expensive, Lower mileage, Third row space adequate only",
            List.of(
                createVariant("Smart", 16.19, Transmission.MANUAL, FuelType.DIESEL, "Basic features"),
                createVariant("Pure+", 18.39, Transmission.MANUAL, FuelType.DIESEL, "Touchscreen, Cruise control"),
                createVariant("Adventure+", 22.89, Transmission.MANUAL, FuelType.DIESEL, "Sunroof, Captain seats"),
                createVariant("Accomplished+ AT", 27.34, Transmission.AUTOMATIC, FuelType.DIESEL, "All features, Auto")
            ),
            List.of(
                createReview(4.6, "Best 7-seater premium SUV. Very safe and comfortable.", 0.91),
                createReview(4.4, "Excellent for families. Great features and space.", 0.88)
            )));
        
        // 17. MG Hector
        cars.add(createCar("MG", "Hector", BodyType.SUV, FuelType.PETROL,
            Transmission.MANUAL, 14.74, 21.96, 14.2, 3.0, 4.2, 587, 5, "1.5L Turbo",
            "14-inch vertical touchscreen, Panoramic sunroof, ADAS, Wireless charging, i-SMART tech",
            "Large touchscreen, Spacious, Feature loaded, Comfortable",
            "Below average reliability, Service network limited, Resale value concerns",
            List.of(
                createVariant("Style", 14.74, Transmission.MANUAL, FuelType.PETROL, "Basic features"),
                createVariant("Smart", 17.20, Transmission.MANUAL, FuelType.PETROL, "Touchscreen, Cruise control"),
                createVariant("Sharp", 19.28, Transmission.MANUAL, FuelType.PETROL, "Sunroof, Connected tech"),
                createVariant("Savvy AT", 21.96, Transmission.AUTOMATIC, FuelType.PETROL, "All features, Auto")
            ),
            List.of(
                createReview(4.0, "Largest touchscreen. Good features but reliability is a concern.", 0.65),
                createReview(4.2, "Spacious and comfortable but quality could be better.", 0.72)
            )));
        
        // 18. Toyota Innova Hycross
        cars.add(createCar("Toyota", "Innova Hycross", BodyType.MPV, FuelType.HYBRID,
            Transmission.AUTOMATIC, 19.77, 30.98, 23.2, 3.0, 4.7, 300, 7, "2.0L Hybrid",
            "10.1-inch touchscreen, Panoramic sunroof, Ventilated seats, Ottoman seats, JBL audio",
            "Hybrid efficiency, Toyota reliability, Premium MPV, Comfortable, Feature rich",
            "Very expensive, Boot space limited, No diesel option",
            List.of(
                createVariant("GX", 19.77, Transmission.AUTOMATIC, FuelType.HYBRID, "Basic features"),
                createVariant("VX", 24.68, Transmission.AUTOMATIC, FuelType.HYBRID, "Touchscreen, Cruise control"),
                createVariant("ZX", 28.14, Transmission.AUTOMATIC, FuelType.HYBRID, "Sunroof, Ottoman seats"),
                createVariant("ZX(O)", 30.98, Transmission.AUTOMATIC, FuelType.HYBRID, "All features, Panoramic sunroof")
            ),
            List.of(
                createReview(4.8, "Best MPV in India. Ultimate comfort and reliability.", 0.95),
                createReview(4.6, "Premium hybrid MPV. Worth every penny.", 0.92)
            )));
        
        // 19. BYD Atto 3
        cars.add(createCar("BYD", "Atto 3", BodyType.SUV, FuelType.ELECTRIC,
            Transmission.AUTOMATIC, 33.99, 34.49, 521.0, 5.0, 4.1, 440, 5, "60.48 kWh Battery",
            "12.8-inch rotating touchscreen, Panoramic sunroof, Wireless charging, ADAS, Voice control",
            "Long range, Feature loaded, Spacious, Good build quality, Fast charging",
            "Very expensive, Charging infrastructure concerns, Service network limited",
            List.of(
                createVariant("Dynamic", 33.99, Transmission.AUTOMATIC, FuelType.ELECTRIC, "521 km range, All features"),
                createVariant("Premium", 34.49, Transmission.AUTOMATIC, FuelType.ELECTRIC, "521 km range, Enhanced features")
            ),
            List.of(
                createReview(4.2, "Best electric SUV under 35 lakhs. Great range and features.", 0.82),
                createReview(4.0, "Good EV but charging infrastructure is a concern.", 0.75)
            )));
        
        // 20. Tata Tiago EV
        cars.add(createCar("Tata", "Tiago EV", BodyType.HATCHBACK, FuelType.ELECTRIC,
            Transmission.AUTOMATIC, 7.99, 11.89, 315.0, 4.0, 4.0, 242, 5, "26 kWh Battery",
            "7-inch touchscreen, Connected features, Regenerative braking, Multiple drive modes",
            "Affordable EV, Good range for city, Zippy performance, Low running cost",
            "Limited range, Charging time, Rear space tight",
            List.of(
                createVariant("XE", 7.99, Transmission.AUTOMATIC, FuelType.ELECTRIC, "250 km range, Basic features"),
                createVariant("XZ+ Tech", 9.99, Transmission.AUTOMATIC, FuelType.ELECTRIC, "315 km range, Touchscreen"),
                createVariant("XZ+ Tech Lux", 11.89, Transmission.AUTOMATIC, FuelType.ELECTRIC, "315 km range, All features")
            ),
            List.of(
                createReview(4.1, "Best affordable EV. Great for city use.", 0.8),
                createReview(3.9, "Good value EV but range could be better.", 0.72)
            )));
        
        // 21. Tata Nexon EV
        cars.add(createCar("Tata", "Nexon EV", BodyType.COMPACT_SUV, FuelType.ELECTRIC,
            Transmission.AUTOMATIC, 14.49, 19.29, 465.0, 5.0, 4.3, 350, 5, "40.5 kWh Battery",
            "10.25-inch touchscreen, Wireless charging, Sunroof, Connected features, ADAS",
            "Long range, 5-star safety, Feature rich, Zippy performance, Good build",
            "Expensive, Charging infrastructure, Service experience varies",
            List.of(
                createVariant("Prime", 14.49, Transmission.AUTOMATIC, FuelType.ELECTRIC, "325 km range, Basic features"),
                createVariant("Max", 16.99, Transmission.AUTOMATIC, FuelType.ELECTRIC, "453 km range, Enhanced features"),
                createVariant("Max Lux", 19.29, Transmission.AUTOMATIC, FuelType.ELECTRIC, "465 km range, All features")
            ),
            List.of(
                createReview(4.4, "Best EV in segment. Great range and safety.", 0.88),
                createReview(4.2, "Feature loaded EV. Good for long drives too.", 0.83)
            )));
        
        // 22. Maruti Brezza
        cars.add(createCar("Maruti", "Brezza", BodyType.COMPACT_SUV, FuelType.PETROL,
            Transmission.MANUAL, 8.34, 14.14, 19.8, 4.0, 4.3, 328, 5, "1.5L K-Series",
            "9-inch SmartPlay Pro, Sunroof, HUD, Cruise control, 360-degree camera",
            "Good mileage, Reliable, Affordable, Feature rich, Low maintenance",
            "Underpowered engine, Average build quality, No diesel option",
            List.of(
                createVariant("LXi", 8.34, Transmission.MANUAL, FuelType.PETROL, "Basic features"),
                createVariant("VXi", 10.14, Transmission.MANUAL, FuelType.PETROL, "Touchscreen, Alloys"),
                createVariant("ZXi", 12.24, Transmission.MANUAL, FuelType.PETROL, "Sunroof, HUD"),
                createVariant("ZXi+ AT", 14.14, Transmission.AUTOMATIC, FuelType.PETROL, "All features, Auto")
            ),
            List.of(
                createReview(4.4, "Best value compact SUV. Very reliable.", 0.85),
                createReview(4.2, "Good mileage and low maintenance.", 0.82)
            )));
        
        // 23. Kia Sonet
        cars.add(createCar("Kia", "Sonet", BodyType.COMPACT_SUV, FuelType.PETROL,
            Transmission.MANUAL, 7.99, 15.72, 18.2, 3.0, 4.3, 392, 5, "1.0L Turbo",
            "10.25-inch touchscreen, Bose audio, Sunroof, Ventilated seats, Connected tech",
            "Feature loaded, Multiple engine options, Good looks, Practical",
            "Ride quality average, Service costs higher",
            List.of(
                createVariant("HTE", 7.99, Transmission.MANUAL, FuelType.PETROL, "Basic features"),
                createVariant("HTK", 10.15, Transmission.MANUAL, FuelType.PETROL, "Touchscreen, Alloys"),
                createVariant("GTX+", 13.69, Transmission.MANUAL, FuelType.PETROL, "Sunroof, Digital cluster"),
                createVariant("GTX+ DCT", 15.72, Transmission.AUTOMATIC, FuelType.PETROL, "All features, DCT")
            ),
            List.of(
                createReview(4.4, "Feature packed compact SUV. Great value.", 0.86),
                createReview(4.2, "Good engine options and features.", 0.81)
            )));
        
        // 24. Renault Kiger
        cars.add(createCar("Renault", "Kiger", BodyType.COMPACT_SUV, FuelType.PETROL,
            Transmission.MANUAL, 6.00, 11.23, 19.2, 3.0, 4.0, 405, 5, "1.0L Turbo",
            "8-inch touchscreen, Wireless charging, Cruise control, Air purifier",
            "Affordable, Spacious boot, Turbo engine, Good ground clearance",
            "Average build quality, Limited service network, Features could be better",
            List.of(
                createVariant("RXE", 6.00, Transmission.MANUAL, FuelType.PETROL, "Basic features"),
                createVariant("RXL", 7.48, Transmission.MANUAL, FuelType.PETROL, "Touchscreen, Alloys"),
                createVariant("RXZ", 9.29, Transmission.MANUAL, FuelType.PETROL, "Digital cluster, Cruise control"),
                createVariant("RXZ Turbo CVT", 11.23, Transmission.AUTOMATIC, FuelType.PETROL, "All features, CVT")
            ),
            List.of(
                createReview(4.0, "Good value for money. Spacious boot.", 0.75),
                createReview(3.9, "Affordable but build quality could be better.", 0.68)
            )));
        
        // 25. Nissan Magnite
        cars.add(createCar("Nissan", "Magnite", BodyType.COMPACT_SUV, FuelType.PETROL,
            Transmission.MANUAL, 6.00, 11.11, 19.7, 4.0, 4.1, 336, 5, "1.0L Turbo",
            "8-inch touchscreen, Wireless charging, 360-degree camera, Cruise control",
            "Affordable, Turbo engine, Feature rich, Good safety",
            "Build quality concerns, Service network limited, Resale value",
            List.of(
                createVariant("XE", 6.00, Transmission.MANUAL, FuelType.PETROL, "Basic features"),
                createVariant("XL", 7.48, Transmission.MANUAL, FuelType.PETROL, "Touchscreen, Alloys"),
                createVariant("XV Premium", 9.49, Transmission.MANUAL, FuelType.PETROL, "Digital cluster, 360 camera"),
                createVariant("XV Premium Turbo CVT", 11.11, Transmission.AUTOMATIC, FuelType.PETROL, "All features, CVT")
            ),
            List.of(
                createReview(4.1, "Best budget compact SUV. Good features and safety.", 0.78),
                createReview(3.9, "Value for money but service network is a concern.", 0.71)
            )));
        
        return cars;
    }
    
    private Car createCar(String make, String model, BodyType bodyType, FuelType fuelType,
                         Transmission transmission, Double priceMin, Double priceMax, Double mileage,
                         Double safetyRating, Double userRating, Integer bootSpace, Integer seats,
                         String engine, String features, String pros, String cons,
                         List<Variant> variants, List<Review> reviews) {
        Car car = Car.builder()
            .make(make)
            .model(model)
            .bodyType(bodyType)
            .fuelType(fuelType)
            .transmission(transmission)
            .priceMin(priceMin)
            .priceMax(priceMax)
            .mileage(mileage)
            .safetyRating(safetyRating)
            .userRating(userRating)
            .bootSpace(bootSpace)
            .seats(seats)
            .engine(engine)
            .features(features)
            .pros(pros)
            .cons(cons)
            .build();
        
        car.setVariants(variants);
        car.setReviews(reviews);
        
        return car;
    }
    
    private Variant createVariant(String name, Double price, Transmission transmission, 
                                 FuelType fuelType, String keyFeatures) {
        return Variant.builder()
            .name(name)
            .price(price)
            .transmission(transmission)
            .fuelType(fuelType)
            .keyFeatures(keyFeatures)
            .build();
    }
    
    private Review createReview(Double rating, String reviewText, Double sentimentScore) {
        return Review.builder()
            .rating(rating)
            .reviewText(reviewText)
            .sentimentScore(sentimentScore)
            .build();
    }
}
