package com.farmreports.api.repository;

import com.farmreports.api.entity.Employee;
import com.farmreports.api.entity.EmploymentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

    List<Employee> findByFarmIdAndStatusAndEmploymentType(Integer farmId, String status, EmploymentType type);

    List<Employee> findByFarmIdOrderByFirstNameAscLastNameAsc(Integer farmId);

    List<Employee> findByFarmIdAndEmploymentTypeOrderByFirstNameAscLastNameAsc(Integer farmId, EmploymentType type);

    @Query("SELECT e FROM Employee e WHERE e.farm.id = :farmId " +
           "AND (LOWER(e.firstName) LIKE LOWER(CONCAT('%', :q, '%')) " +
           "  OR LOWER(e.lastName)  LIKE LOWER(CONCAT('%', :q, '%')) " +
           "  OR LOWER(e.lsNumber)  LIKE LOWER(CONCAT('%', :q, '%'))) " +
           "ORDER BY e.firstName, e.lastName")
    List<Employee> searchByFarmId(Integer farmId, String q);

    Optional<Employee> findByIdAndFarmId(Integer id, Integer farmId);

    Optional<Employee> findByIdAndFarmIdAndEmploymentType(Integer id, Integer farmId, EmploymentType type);

    long countByFarmIdAndStatusAndEmploymentType(Integer farmId, String status, EmploymentType type);
}
