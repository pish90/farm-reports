package com.farmreports.api.repository;

import com.farmreports.api.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department, Integer> {

    List<Department> findByFarmIdOrderByName(Integer farmId);

    Optional<Department> findByIdAndFarmId(Integer id, Integer farmId);
}
