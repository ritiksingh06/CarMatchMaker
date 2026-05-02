package com.example.carmatchmaker.repository;

import com.example.carmatchmaker.model.ShortlistItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShortlistRepository extends JpaRepository<ShortlistItem, Long> {
    
    List<ShortlistItem> findAllByOrderByCreatedAtDesc();
    
    Optional<ShortlistItem> findByCarId(Long carId);
    
    boolean existsByCarId(Long carId);
    
    void deleteByCarId(Long carId);
}
