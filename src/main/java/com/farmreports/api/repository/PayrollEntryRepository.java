package com.farmreports.api.repository;

import com.farmreports.api.entity.PayrollEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PayrollEntryRepository extends JpaRepository<PayrollEntry, Integer> {

    List<PayrollEntry> findByFarmIdAndYearAndMonth(Integer farmId, Integer year, Integer month);

    List<PayrollEntry> findByFarmIdAndYearAndMonthOrderByEmployeeId(Integer farmId, Integer year, Integer month);

    Optional<PayrollEntry> findByFarmIdAndYearAndMonthAndEmployeeId(Integer farmId, Integer year, Integer month, Integer employeeId);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM PayrollEntry p WHERE p.farmId = :farmId AND p.year = :year AND p.month = :month")
    void deleteByFarmIdAndYearAndMonth(@Param("farmId") Integer farmId, @Param("year") Integer year, @Param("month") Integer month);
}
