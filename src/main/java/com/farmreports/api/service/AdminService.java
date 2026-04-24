package com.farmreports.api.service;

import com.farmreports.api.dto.FarmLiveStatusDto;
import com.farmreports.api.dto.FarmSummaryDto;
import com.farmreports.api.dto.ReportDto;
import com.farmreports.api.entity.*;
import com.farmreports.api.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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
    private final AttendanceRepository attendanceRepository;
    private final LivestockReturnRepository livestockReturnRepository;
    private final WorkerRepository workerRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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

    public List<FarmLiveStatusDto> getFarmLiveStatus(int year, int month) {
        return farmRepository.findAll().stream().map(farm -> {
            var report = reportRepository.findByFarmIdAndYearAndMonth(farm.getId(), year, month);

            String reportStatus = report.map(r -> r.getStatus().name()).orElse("NOT_STARTED");
            Integer reportId = report.map(MonthlyReport::getId).orElse(null);

            long attendanceDays = reportId != null
                    ? attendanceRepository.countDistinctDaysByReportId(reportId) : 0;
            long expenseCount = reportId != null
                    ? expenseRepository.countByReportId(reportId) : 0;
            boolean livestockEntered = reportId != null
                    && livestockReturnRepository.existsByReportId(reportId);

            BigDecimal milk = milkRepository.sumLitresByFarmAndYearAndMonth(farm.getId(), year, month);
            BigDecimal expenses = expenseRepository.sumCostByFarmAndYearAndMonth(farm.getId(), year, month);
            long activeWorkers = workerRepository.countByFarmIdAndActiveTrue(farm.getId());

            return new FarmLiveStatusDto(
                    farm.getId(), farm.getName(), year, month,
                    reportStatus, reportId,
                    (int) activeWorkers, attendanceDays,
                    milk != null ? milk.doubleValue() : 0.0,
                    expenseCount,
                    expenses != null ? expenses.doubleValue() : 0.0,
                    livestockEntered
            );
        }).toList();
    }

    @Transactional
    public void resetUserPassword(String email, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No user found with that email"));
        user.setPasswordHash(passwordEncoder.encode(newPassword));
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
