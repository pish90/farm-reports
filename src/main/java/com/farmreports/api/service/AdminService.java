package com.farmreports.api.service;

import com.farmreports.api.dto.FarmSummaryDto;
import com.farmreports.api.dto.ReportDto;
import com.farmreports.api.entity.*;
import com.farmreports.api.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {

    private final FarmRepository farmRepository;
    private final MonthlyReportRepository reportRepository;
    private final MilkProductionRepository milkRepository;
    private final ExpenseRepository expenseRepository;

    public List<FarmSummaryDto> getAllFarmSummaries() {
        int year = LocalDate.now().getYear();
        int month = LocalDate.now().getMonthValue();

        return farmRepository.findAll().stream().map(farm -> {
            LocalDateTime lastSubmitted = reportRepository
                .findFirstByFarm_IdAndStatusOrderBySubmittedAtDesc(farm.getId(), ReportStatus.SUBMITTED)
                .map(MonthlyReport::getSubmittedAt)
                .orElse(null);
            long reportsThisYear = reportRepository.countByFarm_IdAndYear(farm.getId(), year);
            BigDecimal milk = milkRepository.sumLitresByFarmAndYearAndMonth(farm.getId(), year, month);
            BigDecimal expenses = expenseRepository.sumCostByFarmAndYearAndMonth(farm.getId(), year, month);
            return new FarmSummaryDto(
                farm.getId(), farm.getName(), lastSubmitted, reportsThisYear,
                milk != null ? milk.doubleValue() : 0.0,
                expenses != null ? expenses.doubleValue() : 0.0
            );
        }).toList();
    }

    public List<ReportDto> listReports(Integer farmId, Integer year, Integer month, String status) {
        Specification<MonthlyReport> spec = Specification.where(null);
        if (farmId != null)
            spec = spec.and((root, q, cb) -> cb.equal(root.get("farm").get("id"), farmId));
        if (year != null)
            spec = spec.and((root, q, cb) -> cb.equal(root.get("year"), year));
        if (month != null)
            spec = spec.and((root, q, cb) -> cb.equal(root.get("month"), month));
        if (status != null)
            spec = spec.and((root, q, cb) -> cb.equal(root.get("status"), ReportStatus.valueOf(status)));

        return reportRepository.findAll(spec).stream()
            .sorted((a, b) -> {
                int yc = b.getYear().compareTo(a.getYear());
                return yc != 0 ? yc : b.getMonth().compareTo(a.getMonth());
            })
            .map(r -> new ReportDto(
                r.getId(), r.getFarm().getId(), r.getYear(), r.getMonth(),
                r.getStatus().name(), r.getSubmittedAt(), r.getCreatedAt(),
                null, null, null, null
            ))
            .toList();
    }
}
