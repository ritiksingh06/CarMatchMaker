package com.example.carmatchmaker.model;

import com.example.carmatchmaker.enums.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "buyer_preferences")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BuyerPreference {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Double budgetMin;
    
    @Column(nullable = false)
    private Double budgetMax;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UseCase useCase;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BodyType bodyTypePreference;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FuelType fuelPreference;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Transmission transmissionPreference;
    
    @ElementCollection(targetClass = Priority.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "buyer_priorities", joinColumns = @JoinColumn(name = "buyer_preference_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "priority")
    @Builder.Default
    private Set<Priority> priorities = new HashSet<>();
    
    @ElementCollection(targetClass = MustHave.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "buyer_must_haves", joinColumns = @JoinColumn(name = "buyer_preference_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "must_have")
    @Builder.Default
    private Set<MustHave> mustHaves = new HashSet<>();
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
