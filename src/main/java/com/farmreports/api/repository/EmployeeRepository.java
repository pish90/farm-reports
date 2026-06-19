package com.farmreports.api.repository;

import com.farmreports.api.entity.Employee;
import com.farmreports.api.entity.EmploymentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

    List<Employee> findByFarmIdAndStatusAndEmploymentType(Integer farmId, String status, EmploymentType type);

    Optional<Employee> findByIdAndFarmId(Integer id, Integer farmId);

    Optional<Employee> findByIdAndFarmIdAndEmploymentType(Integer id, Integer farmId, EmploymentType type);

    long countByFarmIdAndStatusAndEmploymentType(Integer farmId, String status, EmploymentType type);
}
