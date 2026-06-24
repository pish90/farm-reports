package com.farmreports.api.repository;

import com.farmreports.api.entity.PayrollEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PayrollEntryRepository extends JpaRepository<PayrollEntry, Integer> {

    List<PayrollEntry> findByFarmIdAndYearAndMonth(Integer farmId, Integer year, Integer month);

    List<PayrollEntry> findByFarmIdAndYearAndMonthOrderByEmployeeId(Integer farmId, Integer year, Integer month);

    void deleteByFarmIdAndYearAndMonth(Integer farmId, Integer year, Integer month);
}
