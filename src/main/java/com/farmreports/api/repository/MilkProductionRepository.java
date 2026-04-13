package com.farmreports.api.repository;

import com.farmreports.api.entity.MilkProduction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface MilkProductionRepository extends JpaRepository<MilkProduction, Integer> {

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM MilkProduction mp WHERE mp.report.id = :reportId")
    void deleteByReportId(@Param("reportId") Integer reportId);

    @Query("SELECT COALESCE(SUM(m.litres), 0) FROM MilkProduction m WHERE m.report.farm.id = :farmId AND m.report.year = :year AND m.report.month = :month")
    BigDecimal sumLitresByFarmAndYearAndMonth(@Param("farmId") Integer farmId, @Param("year") Integer year, @Param("month") Integer month);
}
