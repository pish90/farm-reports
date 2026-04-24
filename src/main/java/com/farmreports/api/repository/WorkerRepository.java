package com.farmreports.api.repository;

import com.farmreports.api.entity.Worker;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkerRepository extends JpaRepository<Worker, Integer> {
    List<Worker> findByFarmIdAndActiveTrue(Integer farmId);
    Optional<Worker> findByIdAndFarmId(Integer id, Integer farmId);
    long countByFarmIdAndActiveTrue(Integer farmId);
}
