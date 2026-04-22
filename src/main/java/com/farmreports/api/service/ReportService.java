package com.farmreports.api.service;

import com.farmreports.api.dto.*;
import com.farmreports.api.entity.*;
import com.farmreports.api.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReportService {

    private final MonthlyReportRepository reportRepository;
    private final AttendanceRepository attendanceRepository;
    private final LivestockReturnRepository livestockReturnRepository;
    private final MilkProductionRepository milkProductionRepository;
    private final ExpenseRepository expenseRepository;
    private final WorkerRepository workerRepository;
    private final LivestockTypeRepository livestockTypeRepository;
    private final FarmRepository farmRepository;
    private final UserRepository userRepository;
    private final ExpenseCategoryRepository categoryRepository;
    private final BusinessUnitRepository businessUnitRepository;
    private final JdbcTemplate jdbc;

    @Transactional(readOnly = true)
    public ReportDto getReport(Integer farmId, Integer year, Integer month) {
        MonthlyReport report = reportRepository.findByFarmIdAndYearAndMonth(farmId, year, month)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Report not found"));
        return toDto(report);
    }

    public ReportDto createOrGetReport(Integer farmId, Integer year, Integer month, Integer userId) {
        return reportRepository.findByFarmIdAndYearAndMonth(farmId, year, month)
                .map(this::toDto)
                .orElseGet(() -> {
                    MonthlyReport report = new MonthlyReport();
                    report.setFarm(farmRepository.getReferenceById(farmId));
                    report.setUser(userRepository.getReferenceById(userId));
                    report.setYear(year);
                    report.setMonth(month);
                    return toDto(reportRepository.save(report));
                });
    }

    public void upsertAttendance(Integer reportId, Integer farmId, List<AttendanceEntryRequest> entries) {
        MonthlyReport report = loadReportForFarm(reportId, farmId);
        attendanceRepository.deleteByReportId(reportId);

        List<Attendance> records = entries.stream().map(e -> {
            Worker worker = workerRepository.findByIdAndFarmId(e.workerId(), farmId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Worker not found: " + e.workerId()));
            Attendance a = new Attendance();
            a.setReport(report);
            a.setWorker(worker);
            a.setDayOfMonth(e.dayOfMonth());
            a.setPresent(e.present());
            a.setNotes(e.notes());
            return a;
        }).toList();

        attendanceRepository.saveAll(records);
    }

    public void upsertAttendanceNotes(Integer reportId, Integer farmId, NoteRequest request) {
        loadReportForFarm(reportId, farmId);
        jdbc.update("DELETE FROM attendance_worker_notes WHERE report_id = ?", reportId);
        for (NoteRequest.NoteEntry entry : request.notes()) {
            if (entry.note() != null && !entry.note().isBlank()) {
                jdbc.update(
                    "INSERT INTO attendance_worker_notes (report_id, worker_id, note) VALUES (?, ?, ?)",
                    reportId, entry.subjectId(), entry.note().trim()
                );
            }
        }
    }

    public void upsertLivestockNotes(Integer reportId, Integer farmId, NoteRequest request) {
        loadReportForFarm(reportId, farmId);
        jdbc.update("DELETE FROM livestock_category_notes WHERE report_id = ?", reportId);
        for (NoteRequest.NoteEntry entry : request.notes()) {
            if (entry.subjectKey() != null && entry.note() != null && !entry.note().isBlank()) {
                jdbc.update(
                    "INSERT INTO livestock_category_notes (report_id, category, note) VALUES (?, ?, ?)",
                    reportId, entry.subjectKey(), entry.note().trim()
                );
            }
        }
    }

    public void upsertLivestock(Integer reportId, Integer farmId, List<LivestockEntryRequest> entries) {
        MonthlyReport report = loadReportForFarm(reportId, farmId);
        livestockReturnRepository.deleteByReportId(reportId);

        List<LivestockReturn> records = entries.stream().map(e -> {
            LivestockType type = livestockTypeRepository.findByIdAndFarmId(e.livestockTypeId(), farmId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Livestock type not found: " + e.livestockTypeId()));
            LivestockReturn lr = new LivestockReturn();
            lr.setReport(report);
            lr.setLivestockType(type);
            lr.setCount(e.count());
            return lr;
        }).toList();

        livestockReturnRepository.saveAll(records);
    }

    public void upsertMilk(Integer reportId, Integer farmId, List<MilkEntryRequest> entries) {
        MonthlyReport report = loadReportForFarm(reportId, farmId);
        milkProductionRepository.deleteByReportId(reportId);

        List<MilkProduction> records = entries.stream().map(e -> {
            MilkProduction mp = new MilkProduction();
            mp.setReport(report);
            mp.setDayOfMonth(e.dayOfMonth());
            mp.setLitres(e.litres());
            return mp;
        }).toList();

        milkProductionRepository.saveAll(records);
    }

    public void upsertExpenses(Integer reportId, Integer farmId, List<ExpenseEntryRequest> entries) {
        MonthlyReport report = loadReportForFarm(reportId, farmId);
        expenseRepository.deleteByReportId(reportId);

        List<Expense> records = entries.stream().map(e -> {
            Expense exp = new Expense();
            exp.setReport(report);
            exp.setEntryNo(e.entryNo());
            exp.setDate(e.date());
            exp.setSupplierContractor(e.supplierContractor());
            exp.setReceiptNo(e.receiptNo());
            exp.setCost(e.cost());
            exp.setDescription(e.description());

            if (e.categoryId() != null) {
                exp.setCategory(categoryRepository.getReferenceById(e.categoryId()));
            }
            if (e.businessUnitId() != null) {
                exp.setBusinessUnit(businessUnitRepository.getReferenceById(e.businessUnitId()));
            }

            if (e.apportionments() != null) {
                for (ExpenseEntryRequest.ApportionmentRequest ap : e.apportionments()) {
                    ExpenseApportionment apportionment = new ExpenseApportionment();
                    apportionment.setExpense(exp);
                    apportionment.setBusinessUnit(businessUnitRepository.getReferenceById(ap.businessUnitId()));
                    apportionment.setPercentage(ap.percentage());
                    apportionment.setAmount(ap.amount());
                    exp.getApportionments().add(apportionment);
                }
            }

            return exp;
        }).toList();

        expenseRepository.saveAll(records);
    }

    public ReportDto submitReport(Integer reportId, Integer farmId) {
        MonthlyReport report = loadReportForFarm(reportId, farmId);

        if (report.getStatus() == ReportStatus.SUBMITTED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Report already submitted");
        }
        if (report.getAttendance().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "Attendance section must not be empty before submitting");
        }
        if (report.getLivestockReturns().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "Livestock section must not be empty before submitting");
        }
        if (report.getMilkProduction().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "Milk production section must not be empty before submitting");
        }

        report.setStatus(ReportStatus.SUBMITTED);
        report.setSubmittedAt(LocalDateTime.now());

        return toDto(reportRepository.save(report));
    }

    @Transactional(readOnly = true)
    public ReportDto getReportById(Integer id, Integer farmId, String role) {
        MonthlyReport report;
        if ("ADMIN".equals(role) || "MANAGER".equals(role)) {
            report = reportRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Report not found"));
        } else {
            report = loadReportForFarm(id, farmId);
        }
        return toDto(report);
    }

    private MonthlyReport loadReportForFarm(Integer reportId, Integer farmId) {
        return reportRepository.findByIdAndFarmId(reportId, farmId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Report not found"));
    }

    private ReportDto toDto(MonthlyReport report) {
        List<AttendanceRecordDto> attendance = report.getAttendance().stream()
                .map(a -> new AttendanceRecordDto(
                        a.getId(),
                        a.getWorker().getId(),
                        a.getWorker().getName(),
                        a.getDayOfMonth(),
                        a.isPresent(),
                        a.getNotes()))
                .toList();

        List<LivestockRecordDto> livestock = report.getLivestockReturns().stream()
                .map(lr -> new LivestockRecordDto(
                        lr.getId(),
                        lr.getLivestockType().getId(),
                        lr.getLivestockType().getCategory().name(),
                        lr.getLivestockType().getType(),
                        lr.getCount()))
                .toList();

        List<MilkRecordDto> milk = report.getMilkProduction().stream()
                .map(mp -> new MilkRecordDto(mp.getId(), mp.getDayOfMonth(), mp.getLitres()))
                .toList();

        List<ExpenseRecordDto> expenses = report.getExpenses().stream()
                .map(e -> new ExpenseRecordDto(
                        e.getId(),
                        e.getEntryNo(),
                        e.getDate(),
                        e.getSupplierContractor(),
                        e.getReceiptNo(),
                        e.getCost(),
                        e.getDescription(),
                        e.getCategory() != null ? e.getCategory().getId() : null,
                        e.getCategory() != null ? e.getCategory().getAccountCode() : null,
                        e.getCategory() != null ? e.getCategory().getAccountName() : null,
                        e.getBusinessUnit() != null ? e.getBusinessUnit().getId() : null,
                        e.getBusinessUnit() != null ? e.getBusinessUnit().getCode() : null,
                        e.getBusinessUnit() != null ? e.getBusinessUnit().getName() : null,
                        e.getApportionments().stream()
                                .map(ap -> new ExpenseRecordDto.ApportionmentDto(
                                        ap.getBusinessUnit().getId(),
                                        ap.getBusinessUnit().getCode(),
                                        ap.getBusinessUnit().getName(),
                                        ap.getPercentage(),
                                        ap.getAmount()))
                                .toList()))
                .toList();

        return new ReportDto(
                report.getId(),
                report.getFarm().getId(),
                report.getYear(),
                report.getMonth(),
                report.getStatus().name(),
                report.getSubmittedAt(),
                report.getCreatedAt(),
                attendance,
                livestock,
                milk,
                expenses
        );
    }
}
