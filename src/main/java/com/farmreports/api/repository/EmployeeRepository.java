package com.farmreports.api.repository;

import com.farmreports.api.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Integer>, JpaSpecificationExecutor<Employee> {

    List<Employee> findByFarmIdAndStatusAndSalariedTrue(Integer farmId, String status);

    /** All salaried employees regardless of status, so someone who left mid-year still shows
     *  their earned-this-year totals in the annual payroll view. */
    List<Employee> findByFarmIdAndSalariedTrueOrderByFirstNameAscLastNameAsc(Integer farmId);

    List<Employee> findByFarmIdAndStatusAndCasualTrue(Integer farmId, String status);

    /** All employees at a farm regardless of salaried/casual flag — used by the Casual Labour
     *  page so any employee (not just isCasual=true ones) can be picked for a work session. */
    List<Employee> findByFarmIdAndStatus(Integer farmId, String status);

    @Query("SELECT e FROM Employee e ORDER BY e.farm.name, e.firstName, e.lastName")
    List<Employee> findAllOrderByFarmAndName();

    Optional<Employee> findByIdAndFarmId(Integer id, Integer farmId);

    Optional<Employee> findByIdAndFarmIdAndSalariedTrue(Integer id, Integer farmId);

    Optional<Employee> findByIdAndFarmIdAndCasualTrue(Integer id, Integer farmId);

    long countByFarmIdAndStatusAndSalariedTrue(Integer farmId, String status);

    /** Case/whitespace-insensitive match on (farm, first name, last name); excludeId skips the row being updated. */
    @Query("SELECT COUNT(e) > 0 FROM Employee e WHERE e.farm.id = :farmId " +
           "AND LOWER(TRIM(e.firstName)) = LOWER(TRIM(:firstName)) " +
           "AND COALESCE(LOWER(TRIM(e.lastName)), '') = COALESCE(LOWER(TRIM(:lastName)), '') " +
           "AND (:excludeId IS NULL OR e.id <> :excludeId)")
    boolean existsDuplicate(Integer farmId, String firstName, String lastName, Integer excludeId);
}
