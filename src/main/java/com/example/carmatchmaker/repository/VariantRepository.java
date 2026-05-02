package com.example.carmatchmaker.repository;

import com.example.carmatchmaker.model.Variant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VariantRepository extends JpaRepository<Variant, Long> {
    
    List<Variant> findByCarId(Long carId);
}
