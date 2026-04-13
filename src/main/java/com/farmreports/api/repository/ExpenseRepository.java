package com.farmreports.api.repository;

import com.farmreports.api.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface ExpenseRepository extends JpaRepository<Expense, Integer> {

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM Expense e WHERE e.report.id = :reportId")
    void deleteByReportId(@Param("reportId") Integer reportId);

    @Query("SELECT COALESCE(SUM(e.cost), 0) FROM Expense e WHERE e.report.farm.id = :farmId AND e.report.year = :year AND e.report.month = :month")
    BigDecimal sumCostByFarmAndYearAndMonth(@Param("farmId") Integer farmId, @Param("year") Integer year, @Param("month") Integer month);
}
