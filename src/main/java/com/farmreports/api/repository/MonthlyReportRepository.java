package com.farmreports.api.repository;

import com.farmreports.api.entity.MonthlyReport;
import com.farmreports.api.entity.ReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface MonthlyReportRepository extends JpaRepository<MonthlyReport, Integer>,
        JpaSpecificationExecutor<MonthlyReport> {

    Optional<MonthlyReport> findByFarmIdAndYearAndMonth(Integer farmId, Integer year, Integer month);

    Optional<MonthlyReport> findByIdAndFarmId(Integer id, Integer farmId);

    Optional<MonthlyReport> findFirstByFarm_IdAndStatusOrderBySubmittedAtDesc(Integer farmId, ReportStatus status);

    long countByFarm_IdAndYear(Integer farmId, Integer year);
}
