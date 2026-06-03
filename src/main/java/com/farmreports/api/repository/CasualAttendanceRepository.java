package com.farmreports.api.repository;

import com.farmreports.api.entity.CasualAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface CasualAttendanceRepository extends JpaRepository<CasualAttendance, Integer> {

    List<CasualAttendance> findByReportId(Integer reportId);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM CasualAttendance ca WHERE ca.report.id = :reportId")
    void deleteByReportId(@Param("reportId") Integer reportId);

    @Query(value = """
            SELECT COALESCE(SUM(
                CASE WHEN ca.present = true
                     THEN COALESCE(ca.rate_override, cl.default_daily_rate, 0)
                     ELSE 0 END
            ), 0)
            FROM casual_attendance ca
            JOIN casual_labourers cl ON ca.casual_labourer_id = cl.id
            WHERE ca.casual_labourer_id = :labourerId
            """, nativeQuery = true)
    BigDecimal sumAllTimeEarnedByLabourerId(@Param("labourerId") Integer labourerId);
}
