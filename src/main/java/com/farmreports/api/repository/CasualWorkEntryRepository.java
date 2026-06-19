package com.farmreports.api.repository;

import com.farmreports.api.entity.CasualWorkEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface CasualWorkEntryRepository extends JpaRepository<CasualWorkEntry, Integer> {

    @Query(value = """
            SELECT COALESCE(SUM(COALESCE(e.rate_override, s.default_daily_rate)), 0)
            FROM casual_work_entries e
            JOIN casual_work_sessions s ON e.session_id = s.id
            WHERE e.casual_labourer_id = :employeeId
            """, nativeQuery = true)
    BigDecimal sumEarnedByEmployeeId(@Param("employeeId") Integer employeeId);
}
