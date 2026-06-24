package com.farmreports.api.service;

import com.farmreports.api.dto.PayrollEntryRequest;
import com.farmreports.api.dto.PayrollRecordDto;
import com.farmreports.api.dto.PayrollSummaryDto;
import com.farmreports.api.entity.Employee;
import com.farmreports.api.entity.EmploymentType;
import com.farmreports.api.entity.PayrollEntry;
import com.farmreports.api.repository.EmployeeRepository;
import com.farmreports.api.repository.MonthlyReportRepository;
import com.farmreports.api.repository.PayrollEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PayrollService {

    private final PayrollEntryRepository payrollRepo;
    private final EmployeeRepository employeeRepo;
    private final MonthlyReportRepository reportRepo;
    private final JdbcTemplate jdbc;

    @Transactional
    public List<PayrollRecordDto> getPayroll(Integer farmId, Integer year, Integer month) {
        List<PayrollEntry> existing = payrollRepo.findByFarmIdAndYearAndMonthOrderByEmployeeId(farmId, year, month);
        if (!existing.isEmpty()) {
            return existing.stream().map(this::toDto).toList();
        }

        int prevYear = month > 1 ? year : year - 1;
        int prevMonth = month > 1 ? month - 1 : 12;

        List<PayrollEntry> prevEntries = payrollRepo.findByFarmIdAndYearAndMonth(farmId, prevYear, prevMonth);

        List<PayrollEntry> seeded;
        if (!prevEntries.isEmpty()) {
            seeded = prevEntries.stream().map(prev -> {
                int daysWorked = countDaysWorked(farmId, year, month, prev.getEmployeeId());
                PayrollEntry e = new PayrollEntry();
                e.setFarmId(farmId);
                e.setYear(year);
                e.setMonth(month);
                e.setEmployeeId(prev.getEmployeeId());
                e.setSalaryRate(prev.getSalaryRate());
                e.setLoans(prev.getLoans());
                e.setDaysWorked(daysWorked);
                e.setAmountPaid(BigDecimal.ZERO);
                e.setAmountRemaining(null);
                e.setNotes(null);
                e.setUpdatedAt(LocalDateTime.now());
                return e;
            }).toList();
        } else {
            List<Employee> employees = employeeRepo.findByFarmIdAndStatusAndEmploymentType(
                    farmId, "ACTIVE", EmploymentType.SALARIED);
            seeded = employees.stream().map(emp -> {
                PayrollEntry e = new PayrollEntry();
                e.setFarmId(farmId);
                e.setYear(year);
                e.setMonth(month);
                e.setEmployeeId(emp.getId());
                e.setSalaryRate(null);
                e.setLoans(null);
                e.setDaysWorked(0);
                e.setAmountPaid(BigDecimal.ZERO);
                e.setAmountRemaining(null);
                e.setNotes(null);
                e.setUpdatedAt(LocalDateTime.now());
                return e;
            }).toList();
        }

        List<PayrollEntry> saved = payrollRepo.saveAll(seeded);
        return saved.stream().map(this::toDto).toList();
    }

    @Transactional
    public void upsertPayroll(Integer farmId, Integer year, Integer month, List<PayrollEntryRequest> entries) {
        payrollRepo.deleteByFarmIdAndYearAndMonth(farmId, year, month);

        List<PayrollEntry> toSave = new ArrayList<>();
        for (PayrollEntryRequest req : entries) {
            employeeRepo.findByIdAndFarmId(req.employeeId(), farmId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Employee not found: " + req.employeeId()));

            PayrollEntry e = new PayrollEntry();
            e.setFarmId(farmId);
            e.setYear(year);
            e.setMonth(month);
            e.setEmployeeId(req.employeeId());
            e.setSalaryRate(req.salaryRate());
            e.setDaysWorked(req.daysWorked());
            e.setGrossSalary(req.grossSalary());
            e.setLoans(req.loans());
            e.setAmountPaid(req.amountPaid());
            e.setAmountRemaining(req.amountRemaining());
            e.setNotes(req.notes());
            e.setUpdatedAt(LocalDateTime.now());
            toSave.add(e);
        }

        payrollRepo.saveAll(toSave);
    }

    @Transactional(readOnly = true)
    public List<PayrollSummaryDto> getAnnualPayrollSummary(Integer farmId, Integer year) {
        return jdbc.query(
                "SELECT farm_id, year, month, total_gross, total_loans, total_paid, total_remaining " +
                "FROM summary_payroll WHERE farm_id = ? AND year = ? ORDER BY month",
                (rs, rowNum) -> new PayrollSummaryDto(
                        rs.getInt("farm_id"),
                        rs.getInt("year"),
                        rs.getInt("month"),
                        rs.getBigDecimal("total_gross"),
                        rs.getBigDecimal("total_loans"),
                        rs.getBigDecimal("total_paid"),
                        rs.getBigDecimal("total_remaining")
                ),
                farmId, year
        );
    }

    private int countDaysWorked(Integer farmId, Integer year, Integer month, Integer employeeId) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM attendance a " +
                "JOIN monthly_reports mr ON a.report_id = mr.id " +
                "WHERE mr.farm_id = ? AND mr.year = ? AND mr.month = ? " +
                "AND a.worker_id = ? AND a.status IN ('P','WA')",
                Integer.class,
                farmId, year, month, employeeId
        );
        return count != null ? count : 0;
    }

    private PayrollRecordDto toDto(PayrollEntry e) {
        Employee emp = employeeRepo.findById(e.getEmployeeId()).orElse(null);
        String name = emp != null ? emp.getFullName() : "Unknown";
        String code = emp != null ? emp.getEmployeeId() : null;
        return new PayrollRecordDto(
                e.getId(),
                e.getEmployeeId(),
                name,
                code,
                e.getSalaryRate(),
                e.getDaysWorked(),
                e.getGrossSalary(),
                e.getLoans() != null ? e.getLoans() : BigDecimal.ZERO,
                e.getAmountPaid() != null ? e.getAmountPaid() : BigDecimal.ZERO,
                e.getAmountRemaining(),
                e.getNotes()
        );
    }
}
