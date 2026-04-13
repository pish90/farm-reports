package com.farmreports.api.repository;

import com.farmreports.api.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AttendanceRepository extends JpaRepository<Attendance, Integer> {

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM Attendance a WHERE a.report.id = :reportId")
    void deleteByReportId(@Param("reportId") Integer reportId);
}
