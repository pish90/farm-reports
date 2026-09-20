package com.farmreports.api.service;

import com.farmreports.api.dto.ExpenseListItemDto;
import com.farmreports.api.dto.ExpenseUpdateRequest;
import com.farmreports.api.dto.FarmLiveStatusDto;
import com.farmreports.api.dto.FarmSummaryDto;
import com.farmreports.api.dto.PageDto;
import com.farmreports.api.dto.ReportDto;
import com.farmreports.api.entity.*;
import com.farmreports.api.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {

    private final FarmRepository farmRepository;
    private final MonthlyReportRepository reportRepository;
    private final MilkProductionRepository milkRepository;
    private final ExpenseRepository expenseRepository;
    private final PayrollEntryRepository payrollEntryRepository;
    private final LivestockReturnRepository livestockReturnRepository;
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ExpenseCategoryRepository expenseCategoryRepository;

    /** farmId null → every farm (ADMIN/OPERATIONS_MANAGER); non-null → that farm only (MANAGER,
     *  confined to their own farm by the caller). */
    private List<Farm> farmsInScope(Integer farmId) {
        return farmId != null ? farmRepository.findById(farmId).map(List::of).orElse(List.of()) : farmRepository.findAll();
    }

    public List<FarmSummaryDto> getAllFarmSummaries(Integer farmId, int year, int month) {
        return farmsInScope(farmId).stream().map(farm -> {
            LocalDateTime lastReportAt = reportRepository
                .findFirstByFarm_IdOrderByCreatedAtDesc(farm.getId())
                .map(MonthlyReport::getCreatedAt)
                .orElse(null);
            long reportsThisYear = reportRepository.countByFarm_IdAndYear(farm.getId(), year);
            BigDecimal milk = milkRepository.sumLitresByFarmAndYearAndMonth(farm.getId(), year, month);
            BigDecimal expenses = expenseRepository.sumCostByFarmAndYearAndMonth(farm.getId(), year, month);
            return new FarmSummaryDto(
                farm.getId(), farm.getName(), lastReportAt, reportsThisYear,
                milk != null ? milk.doubleValue() : 0.0,
                expenses != null ? expenses.doubleValue() : 0.0
            );
        }).toList();
    }

    /** farmId null → every farm (ADMIN/OPERATIONS_MANAGER); non-null → that farm only (MANAGER,
     *  confined to their own farm by the caller). */
    public List<FarmLiveStatusDto> getFarmLiveStatus(int year, int month, Integer farmId) {
        return farmsInScope(farmId).stream().map(farm -> {
            var report = reportRepository.findByFarmIdAndYearAndMonth(farm.getId(), year, month);

            String reportStatus = report.map(r -> r.getStatus().name()).orElse("NOT_STARTED");
            Integer reportId = report.map(MonthlyReport::getId).orElse(null);

            long payrollEntriesRecorded = payrollEntryRepository.countByFarmIdAndYearAndMonth(farm.getId(), year, month);
            long expenseCount = reportId != null
                    ? expenseRepository.countByReportId(reportId) : 0;
            boolean livestockEntered = reportId != null
                    && livestockReturnRepository.existsByReportId(reportId);

            BigDecimal milk = milkRepository.sumLitresByFarmAndYearAndMonth(farm.getId(), year, month);
            BigDecimal expenses = expenseRepository.sumCostByFarmAndYearAndMonth(farm.getId(), year, month);
            long activeWorkers = employeeRepository.countByFarmIdAndStatusAndSalariedTrue(
                    farm.getId(), "ACTIVE");

            return new FarmLiveStatusDto(
                    farm.getId(), farm.getName(), year, month,
                    reportStatus, reportId,
                    (int) activeWorkers, payrollEntriesRecorded,
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

    public PageDto<ReportDto> listReports(Integer farmId, Integer year, Integer month,
                                           int page, int size) {
        Specification<MonthlyReport> spec = Specification.where(null);
        if (farmId != null)
            spec = spec.and((root, q, cb) -> cb.equal(root.get("farm").get("id"), farmId));
        if (year != null)
            spec = spec.and((root, q, cb) -> cb.equal(root.get("year"), year));
        if (month != null)
            spec = spec.and((root, q, cb) -> cb.equal(root.get("month"), month));

        Sort sort = Sort.by(Sort.Direction.DESC, "year").and(Sort.by(Sort.Direction.DESC, "month"));
        Page<MonthlyReport> result = reportRepository.findAll(spec, PageRequest.of(page, size, sort));

        List<ReportDto> content = result.getContent().stream()
            .map(r -> new ReportDto(
                r.getId(), r.getFarm().getId(), r.getYear(), r.getMonth(), r.getCreatedAt(),
                null, null, null, null
            ))
            .toList();
        return new PageDto<>(content, result.getTotalElements(), result.getTotalPages(), page, size);
    }

    /** Flat, farm-tagged expense listing across reports for the standalone Expenses page —
     *  distinct from a single report's own Expenses tab, which reads {@code report.expenses}. */
    public PageDto<ExpenseListItemDto> listExpenses(Integer farmId, Integer year, Integer month,
                                                     Integer categoryId, int page, int size) {
        Specification<Expense> spec = Specification.where(null);
        if (farmId != null)
            spec = spec.and((root, q, cb) -> cb.equal(root.get("report").get("farm").get("id"), farmId));
        if (year != null)
            spec = spec.and((root, q, cb) -> cb.equal(root.get("report").get("year"), year));
        if (month != null)
            spec = spec.and((root, q, cb) -> cb.equal(root.get("report").get("month"), month));
        if (categoryId != null)
            spec = spec.and((root, q, cb) -> cb.equal(root.get("category").get("id"), categoryId));

        Sort sort = Sort.by(Sort.Direction.DESC, "date").and(Sort.by(Sort.Direction.DESC, "entryNo"));
        Page<Expense> result = expenseRepository.findAll(spec, PageRequest.of(page, size, sort));

        List<ExpenseListItemDto> content = result.getContent().stream()
                .map(e -> new ExpenseListItemDto(
                        e.getId(),
                        e.getReport().getId(),
                        e.getReport().getFarm().getId(),
                        e.getReport().getFarm().getName(),
                        e.getReport().getYear(),
                        e.getReport().getMonth(),
                        e.getDate(),
                        e.getReceiptNo(),
                        e.getSupplierContractor(),
                        e.getDescription(),
                        e.getCategory() != null ? e.getCategory().getAccountName() : null,
                        e.getCost()
                ))
                .toList();
        return new PageDto<>(content, result.getTotalElements(), result.getTotalPages(), page, size);
    }

    /** Edits a single expense in place (ADMIN only, standalone Expenses page) — distinct from
     *  the whole-report upsert used by the report-detail Expenses tab. */
    @Transactional
    public ExpenseListItemDto updateExpense(Integer id, ExpenseUpdateRequest request) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Expense not found"));

        expense.setDate(request.date());
        expense.setSupplierContractor(blankToNull(request.supplierContractor()));
        expense.setReceiptNo(blankToNull(request.receiptNo()));
        expense.setCost(request.cost());
        expense.setDescription(blankToNull(request.description()));
        expense.setCategory(request.categoryId() != null
                ? expenseCategoryRepository.getReferenceById(request.categoryId()) : null);

        Expense saved = expenseRepository.save(expense);
        return new ExpenseListItemDto(
                saved.getId(),
                saved.getReport().getId(),
                saved.getReport().getFarm().getId(),
                saved.getReport().getFarm().getName(),
                saved.getReport().getYear(),
                saved.getReport().getMonth(),
                saved.getDate(),
                saved.getReceiptNo(),
                saved.getSupplierContractor(),
                saved.getDescription(),
                saved.getCategory() != null ? saved.getCategory().getAccountName() : null,
                saved.getCost()
        );
    }

    private static String blankToNull(String s) {
        return s != null && !s.isBlank() ? s.trim() : null;
    }

    /** Deletes a single expense (ADMIN only, standalone Expenses page). Returns the
     *  deleted row's data for the audit log, since the expense itself is gone after this call. */
    @Transactional
    public ExpenseListItemDto deleteExpense(Integer id) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Expense not found"));
        ExpenseListItemDto dto = new ExpenseListItemDto(
                expense.getId(),
                expense.getReport().getId(),
                expense.getReport().getFarm().getId(),
                expense.getReport().getFarm().getName(),
                expense.getReport().getYear(),
                expense.getReport().getMonth(),
                expense.getDate(),
                expense.getReceiptNo(),
                expense.getSupplierContractor(),
                expense.getDescription(),
                expense.getCategory() != null ? expense.getCategory().getAccountName() : null,
                expense.getCost()
        );
        expenseRepository.delete(expense);
        return dto;
    }
}
