package com.farmreports.api.repository;

import com.farmreports.api.entity.LivestockType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LivestockTypeRepository extends JpaRepository<LivestockType, Integer> {
    List<LivestockType> findByFarmId(Integer farmId);
    Optional<LivestockType> findByIdAndFarmId(Integer id, Integer farmId);
}
