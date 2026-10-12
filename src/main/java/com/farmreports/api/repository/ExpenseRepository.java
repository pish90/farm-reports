package com.farmreports.api.repository;

import com.farmreports.api.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Integer>, JpaSpecificationExecutor<Expense> {

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM Expense e WHERE e.report.id = :reportId")
    void deleteByReportId(@Param("reportId") Integer reportId);

    @Query("SELECT COALESCE(SUM(e.cost), 0) FROM Expense e WHERE e.report.farm.id = :farmId AND e.report.year = :year AND e.report.month = :month")
    BigDecimal sumCostByFarmAndYearAndMonth(@Param("farmId") Integer farmId, @Param("year") Integer year, @Param("month") Integer month);

    long countByReportId(Integer reportId);

    /** A list, not an Optional: receipt numbers typed in by hand aren't unique per farm, so the
     *  same (farm, receiptNo) can legitimately match several existing expenses. */
    List<Expense> findByReport_Farm_IdAndReceiptNoIgnoreCase(Integer farmId, String receiptNo);

    @Query("SELECT COALESCE(MAX(e.entryNo), 0) FROM Expense e WHERE e.report.id = :reportId")
    int findMaxEntryNoByReportId(@Param("reportId") Integer reportId);
}
