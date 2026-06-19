package com.farmreports.api.repository;

import com.farmreports.api.entity.CasualAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CasualAttendanceRepository extends JpaRepository<CasualAttendance, Integer> {

    List<CasualAttendance> findByReportId(Integer reportId);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM CasualAttendance ca WHERE ca.report.id = :reportId")
    void deleteByReportId(@Param("reportId") Integer reportId);
}
