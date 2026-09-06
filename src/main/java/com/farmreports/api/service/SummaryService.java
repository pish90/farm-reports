package com.farmreports.api.service;

import com.farmreports.api.dto.LivestockSummaryDto;
import com.farmreports.api.dto.MilkSummaryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SummaryService {

    private final JdbcTemplate jdbc;

    public List<MilkSummaryDto> getMilkSummary(Integer farmId, Integer year) {
        return jdbc.query(
                "SELECT farm_id, year, month, total_litres FROM summary_milk_prod WHERE farm_id = ? AND year = ? ORDER BY month",
                (rs, rowNum) -> new MilkSummaryDto(
                        rs.getInt("farm_id"),
                        rs.getInt("year"),
                        rs.getInt("month"),
                        rs.getBigDecimal("total_litres") != null ? rs.getBigDecimal("total_litres") : BigDecimal.ZERO
                ),
                farmId, year
        );
    }

    /**
     * Monthly milk totals for one farm across an arbitrary (year, month) range, potentially
     * spanning multiple calendar years — e.g. Mar 2025 through Aug 2026. The range is expressed
     * as a single comparable "year*100+month" key so it can be checked with one BETWEEN.
     */
    public List<MilkSummaryDto> getMilkSummaryRange(
            Integer farmId, Integer startYear, Integer startMonth, Integer endYear, Integer endMonth) {
        int startKey = startYear * 100 + startMonth;
        int endKey = endYear * 100 + endMonth;
        if (startKey > endKey) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start of range must not be after end of range");
        }
        return jdbc.query(
                "SELECT farm_id, year, month, total_litres FROM summary_milk_prod " +
                        "WHERE farm_id = ? AND (year * 100 + month) BETWEEN ? AND ? ORDER BY year, month",
                (rs, rowNum) -> new MilkSummaryDto(
                        rs.getInt("farm_id"),
                        rs.getInt("year"),
                        rs.getInt("month"),
                        rs.getBigDecimal("total_litres") != null ? rs.getBigDecimal("total_litres") : BigDecimal.ZERO
                ),
                farmId, startKey, endKey
        );
    }

    public List<LivestockSummaryDto> getLivestockSummary(Integer farmId, Integer year) {
        return jdbc.query(
                "SELECT farm_id, year, month, category, type, total_count FROM summary_livestock WHERE farm_id = ? AND year = ? ORDER BY month, category, type",
                (rs, rowNum) -> new LivestockSummaryDto(
                        rs.getInt("farm_id"),
                        rs.getInt("year"),
                        rs.getInt("month"),
                        rs.getString("category"),
                        rs.getString("type"),
                        rs.getInt("total_count")
                ),
                farmId, year
        );
    }
}
