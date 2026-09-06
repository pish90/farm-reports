package com.farmreports.api.service;

import com.farmreports.api.dto.ExpenseListItemDto;
import com.farmreports.api.dto.FarmLiveStatusDto;
import com.farmreports.api.dto.FarmSummaryDto;
import com.farmreports.api.dto.PageDto;
import com.farmreports.api.entity.Expense;
import com.farmreports.api.entity.ExpenseCategory;
import com.farmreports.api.entity.Farm;
import com.farmreports.api.entity.MonthlyReport;
import com.farmreports.api.repository.EmployeeRepository;
import com.farmreports.api.repository.ExpenseRepository;
import com.farmreports.api.repository.FarmRepository;
import com.farmreports.api.repository.LivestockReturnRepository;
import com.farmreports.api.repository.MilkProductionRepository;
import com.farmreports.api.repository.MonthlyReportRepository;
import com.farmreports.api.repository.PayrollEntryRepository;
import com.farmreports.api.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock FarmRepository farmRepository;
    @Mock MonthlyReportRepository reportRepository;
    @Mock MilkProductionRepository milkRepository;
    @Mock ExpenseRepository expenseRepository;
    @Mock PayrollEntryRepository payrollEntryRepository;
    @Mock LivestockReturnRepository livestockReturnRepository;
    @Mock EmployeeRepository employeeRepository;
    @Mock UserRepository userRepository;
    @Mock PasswordEncoder passwordEncoder;

    AdminService adminService;

    @Test
    void listExpenses_mapsFarmAndCategoryOntoFlatRow() {
        adminService = new AdminService(farmRepository, reportRepository, milkRepository, expenseRepository,
                payrollEntryRepository, livestockReturnRepository, employeeRepository, userRepository, passwordEncoder);

        Farm matunda = new Farm();
        matunda.setId(1);
        matunda.setName("Matunda");

        MonthlyReport report = new MonthlyReport();
        report.setId(55);
        report.setFarm(matunda);
        report.setYear(2026);
        report.setMonth(1);

        ExpenseCategory fuel = new ExpenseCategory();
        fuel.setId(9);
        fuel.setAccountCode("4000");
        fuel.setAccountName("Fuel");

        Expense expense = new Expense();
        expense.setId(200);
        expense.setReport(report);
        expense.setEntryNo(1);
        expense.setDate(LocalDate.of(2026, 1, 15));
        expense.setReceiptNo("INV-1001");
        expense.setSupplierContractor("ABC Traders");
        expense.setDescription("Diesel");
        expense.setCategory(fuel);
        expense.setCost(new BigDecimal("5400.00"));

        when(expenseRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(expense), PageRequest.of(0, 10), 1));

        PageDto<ExpenseListItemDto> result = adminService.listExpenses(1, 2026, 1, null, 0, 10);

        assertThat(result.totalElements()).isEqualTo(1);
        ExpenseListItemDto row = result.content().get(0);
        assertThat(row.farmId()).isEqualTo(1);
        assertThat(row.farmName()).isEqualTo("Matunda");
        assertThat(row.reportId()).isEqualTo(55);
        assertThat(row.date()).isEqualTo(LocalDate.of(2026, 1, 15));
        assertThat(row.receiptNo()).isEqualTo("INV-1001");
        assertThat(row.supplierContractor()).isEqualTo("ABC Traders");
        assertThat(row.description()).isEqualTo("Diesel");
        assertThat(row.categoryName()).isEqualTo("Fuel");
        assertThat(row.cost()).isEqualByComparingTo("5400.00");
    }

    @Test
    void listExpenses_noCategory_categoryNameIsNull() {
        adminService = new AdminService(farmRepository, reportRepository, milkRepository, expenseRepository,
                payrollEntryRepository, livestockReturnRepository, employeeRepository, userRepository, passwordEncoder);

        Farm lesA = new Farm();
        lesA.setId(2);
        lesA.setName("Les A");

        MonthlyReport report = new MonthlyReport();
        report.setId(60);
        report.setFarm(lesA);
        report.setYear(2026);
        report.setMonth(2);

        Expense expense = new Expense();
        expense.setId(201);
        expense.setReport(report);
        expense.setEntryNo(1);
        expense.setDate(LocalDate.of(2026, 2, 1));
        expense.setCost(BigDecimal.TEN);

        when(expenseRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(expense), PageRequest.of(0, 10), 1));

        PageDto<ExpenseListItemDto> result = adminService.listExpenses(null, null, null, null, 0, 10);

        assertThat(result.content().get(0).categoryName()).isNull();
    }

    // ── Farm isolation regression: a MANAGER's effectiveFarmId must confine these to one farm ──

    private static Farm farm(int id, String name) {
        Farm f = new Farm();
        f.setId(id);
        f.setName(name);
        return f;
    }

    @Test
    void getAllFarmSummaries_withFarmId_returnsOnlyThatFarm_notEveryFarm() {
        adminService = new AdminService(farmRepository, reportRepository, milkRepository, expenseRepository,
                payrollEntryRepository, livestockReturnRepository, employeeRepository, userRepository, passwordEncoder);

        Farm matunda = farm(1, "Matunda");
        when(farmRepository.findById(1)).thenReturn(Optional.of(matunda));
        lenient().when(reportRepository.findFirstByFarm_IdAndStatusOrderBySubmittedAtDesc(anyInt(), any()))
                .thenReturn(Optional.empty());
        lenient().when(reportRepository.countByFarm_IdAndYear(anyInt(), anyInt())).thenReturn(0L);

        List<FarmSummaryDto> result = adminService.getAllFarmSummaries(1);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).farmId()).isEqualTo(1);
        assertThat(result.get(0).farmName()).isEqualTo("Matunda");
        verify(farmRepository, never()).findAll();
    }

    @Test
    void getAllFarmSummaries_nullFarmId_returnsEveryFarm() {
        adminService = new AdminService(farmRepository, reportRepository, milkRepository, expenseRepository,
                payrollEntryRepository, livestockReturnRepository, employeeRepository, userRepository, passwordEncoder);

        when(farmRepository.findAll()).thenReturn(List.of(farm(1, "Matunda"), farm(2, "Les A")));
        lenient().when(reportRepository.findFirstByFarm_IdAndStatusOrderBySubmittedAtDesc(anyInt(), any()))
                .thenReturn(Optional.empty());
        lenient().when(reportRepository.countByFarm_IdAndYear(anyInt(), anyInt())).thenReturn(0L);

        List<FarmSummaryDto> result = adminService.getAllFarmSummaries(null);

        assertThat(result).hasSize(2);
        verify(farmRepository, never()).findById(any());
    }

    @Test
    void getFarmLiveStatus_withFarmId_returnsOnlyThatFarm_notEveryFarm() {
        adminService = new AdminService(farmRepository, reportRepository, milkRepository, expenseRepository,
                payrollEntryRepository, livestockReturnRepository, employeeRepository, userRepository, passwordEncoder);

        Farm matunda = farm(1, "Matunda");
        when(farmRepository.findById(1)).thenReturn(Optional.of(matunda));
        lenient().when(reportRepository.findByFarmIdAndYearAndMonth(anyInt(), anyInt(), anyInt()))
                .thenReturn(Optional.empty());
        lenient().when(payrollEntryRepository.countByFarmIdAndYearAndMonth(anyInt(), anyInt(), anyInt())).thenReturn(0L);
        lenient().when(employeeRepository.countByFarmIdAndStatusAndSalariedTrue(anyInt(), any())).thenReturn(0L);

        List<FarmLiveStatusDto> result = adminService.getFarmLiveStatus(2026, 1, 1);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).farmId()).isEqualTo(1);
        verify(farmRepository, never()).findAll();
    }
}
