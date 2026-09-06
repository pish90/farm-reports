package com.farmreports.api.service;

import com.farmreports.api.dto.MilkSummaryDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SummaryServiceTest {

    @Mock JdbcTemplate jdbc;

    SummaryService summaryService;

    @BeforeEach
    void setUp() {
        summaryService = new SummaryService(jdbc);
    }

    @Test
    void getMilkSummaryRange_spanningYearBoundary_queriesWithCombinedYearMonthKeys() {
        List<MilkSummaryDto> expected = List.of(
                new MilkSummaryDto(1, 2025, 11, BigDecimal.valueOf(500)),
                new MilkSummaryDto(1, 2025, 12, BigDecimal.valueOf(600)),
                new MilkSummaryDto(1, 2026, 1, BigDecimal.valueOf(700))
        );
        when(jdbc.query(anyString(), any(RowMapper.class), eq(1), eq(202511), eq(202601)))
                .thenReturn(expected);

        List<MilkSummaryDto> result = summaryService.getMilkSummaryRange(1, 2025, 11, 2026, 1);

        assertThat(result).isEqualTo(expected);
        verify(jdbc).query(anyString(), any(RowMapper.class), eq(1), eq(202511), eq(202601));
    }

    @Test
    void getMilkSummaryRange_sameYear_buildsKeysWithinThatYear() {
        when(jdbc.query(anyString(), any(RowMapper.class), eq(2), eq(202603), eq(202606)))
                .thenReturn(List.of());

        List<MilkSummaryDto> result = summaryService.getMilkSummaryRange(2, 2026, 3, 2026, 6);

        assertThat(result).isEmpty();
        verify(jdbc).query(anyString(), any(RowMapper.class), eq(2), eq(202603), eq(202606));
    }

    @Test
    void getMilkSummaryRange_startAfterEnd_throwsBadRequest() {
        assertThatThrownBy(() -> summaryService.getMilkSummaryRange(1, 2026, 6, 2026, 1))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Start of range must not be after end of range");
    }

    @Test
    void getMilkSummary_rowMapper_mapsNullTotalLitresToZero() {
        when(jdbc.query(anyString(), any(RowMapper.class), eq(1), eq(2026)))
                .thenAnswer(invocation -> {
                    RowMapper<MilkSummaryDto> mapper = invocation.getArgument(1);
                    java.sql.ResultSet rs = org.mockito.Mockito.mock(java.sql.ResultSet.class);
                    when(rs.getInt("farm_id")).thenReturn(1);
                    when(rs.getInt("year")).thenReturn(2026);
                    when(rs.getInt("month")).thenReturn(5);
                    when(rs.getBigDecimal("total_litres")).thenReturn(null);
                    return List.of(mapper.mapRow(rs, 0));
                });

        List<MilkSummaryDto> result = summaryService.getMilkSummary(1, 2026);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).totalLitres()).isEqualByComparingTo(BigDecimal.ZERO);
    }
}
