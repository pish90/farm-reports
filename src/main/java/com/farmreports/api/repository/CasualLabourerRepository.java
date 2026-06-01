package com.farmreports.api.repository;

import com.farmreports.api.entity.CasualLabourer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CasualLabourerRepository extends JpaRepository<CasualLabourer, Integer> {
    List<CasualLabourer> findByFarmIdAndActiveTrue(Integer farmId);
    Optional<CasualLabourer> findByIdAndFarmId(Integer id, Integer farmId);
}
