package com.farmreports.api.service;

import com.farmreports.api.dto.LivestockSummaryDto;
import com.farmreports.api.dto.MilkSummaryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

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
