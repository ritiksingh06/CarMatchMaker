package com.example.carmatchmaker.repository;

import com.example.carmatchmaker.model.BuyerPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BuyerPreferenceRepository extends JpaRepository<BuyerPreference, Long> {
}
