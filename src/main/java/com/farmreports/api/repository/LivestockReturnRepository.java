package com.farmreports.api.repository;

import com.farmreports.api.entity.LivestockReturn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LivestockReturnRepository extends JpaRepository<LivestockReturn, Integer> {

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM LivestockReturn lr WHERE lr.report.id = :reportId")
    void deleteByReportId(@Param("reportId") Integer reportId);
}
