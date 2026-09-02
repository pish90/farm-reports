package com.farmreports.api.repository;

import com.farmreports.api.entity.Employee;
import com.farmreports.api.entity.EmploymentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Integer>, JpaSpecificationExecutor<Employee> {

    List<Employee> findByFarmIdAndStatusAndEmploymentType(Integer farmId, String status, EmploymentType type);

    @Query("SELECT e FROM Employee e ORDER BY e.farm.name, e.firstName, e.lastName")
    List<Employee> findAllOrderByFarmAndName();

    Optional<Employee> findByIdAndFarmId(Integer id, Integer farmId);

    Optional<Employee> findByIdAndFarmIdAndEmploymentType(Integer id, Integer farmId, EmploymentType type);

    long countByFarmIdAndStatusAndEmploymentType(Integer farmId, String status, EmploymentType type);

    /** Case/whitespace-insensitive match on (farm, first name, last name); excludeId skips the row being updated. */
    @Query("SELECT COUNT(e) > 0 FROM Employee e WHERE e.farm.id = :farmId " +
           "AND LOWER(TRIM(e.firstName)) = LOWER(TRIM(:firstName)) " +
           "AND COALESCE(LOWER(TRIM(e.lastName)), '') = COALESCE(LOWER(TRIM(:lastName)), '') " +
           "AND (:excludeId IS NULL OR e.id <> :excludeId)")
    boolean existsDuplicate(Integer farmId, String firstName, String lastName, Integer excludeId);
}
