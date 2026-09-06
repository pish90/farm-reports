package com.farmreports.api.service;

import com.farmreports.api.dto.ReportDto;
import com.farmreports.api.entity.Farm;
import com.farmreports.api.entity.MonthlyReport;
import com.farmreports.api.repository.BusinessUnitRepository;
import com.farmreports.api.repository.CasualAttendanceRepository;
import com.farmreports.api.repository.EmployeeRepository;
import com.farmreports.api.repository.ExpenseCategoryRepository;
import com.farmreports.api.repository.ExpenseRepository;
import com.farmreports.api.repository.FarmRepository;
import com.farmreports.api.repository.LivestockReturnRepository;
import com.farmreports.api.repository.LivestockTypeRepository;
import com.farmreports.api.repository.MilkProductionRepository;
import com.farmreports.api.repository.MonthlyReportRepository;
import com.farmreports.api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

/** Regression coverage for the cross-tenant report-access bug: a plain MANAGER must never be
 *  able to read a report belonging to a different farm just by knowing its ID. */
@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock MonthlyReportRepository reportRepository;
    @Mock LivestockReturnRepository livestockReturnRepository;
    @Mock MilkProductionRepository milkProductionRepository;
    @Mock ExpenseRepository expenseRepository;
    @Mock EmployeeRepository employeeRepository;
    @Mock LivestockTypeRepository livestockTypeRepository;
    @Mock FarmRepository farmRepository;
    @Mock UserRepository userRepository;
    @Mock ExpenseCategoryRepository categoryRepository;
    @Mock BusinessUnitRepository businessUnitRepository;
    @Mock CasualAttendanceRepository casualAttendanceRepository;
    @Mock JdbcTemplate jdbc;

    ReportService reportService;

    MonthlyReport otherFarmsReport;

    @BeforeEach
    void setUp() {
        reportService = new ReportService(reportRepository, livestockReturnRepository, milkProductionRepository,
                expenseRepository, employeeRepository, livestockTypeRepository, farmRepository, userRepository,
                categoryRepository, businessUnitRepository, casualAttendanceRepository, jdbc);

        Farm otherFarm = new Farm();
        otherFarm.setId(99);
        otherFarm.setName("Someone Else's Farm");

        otherFarmsReport = new MonthlyReport();
        otherFarmsReport.setId(500);
        otherFarmsReport.setFarm(otherFarm);
        otherFarmsReport.setYear(2026);
        otherFarmsReport.setMonth(6);

        lenient().when(casualAttendanceRepository.findByReportId(anyInt())).thenReturn(List.of());
    }

    @Test
    void getReportById_managerFromDifferentFarm_cannotReadReport() {
        // MANAGER at farm 1 requesting report 500, which actually belongs to farm 99.
        when(reportRepository.findByIdAndFarmId(500, 1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reportService.getReportById(500, 1, "MANAGER"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Report not found");
    }

    @Test
    void getReportById_managerFromOwnFarm_canReadReport() {
        Farm ownFarm = new Farm();
        ownFarm.setId(1);
        ownFarm.setName("Matunda");
        MonthlyReport ownReport = new MonthlyReport();
        ownReport.setId(501);
        ownReport.setFarm(ownFarm);
        ownReport.setYear(2026);
        ownReport.setMonth(6);

        when(reportRepository.findByIdAndFarmId(501, 1)).thenReturn(Optional.of(ownReport));

        ReportDto dto = reportService.getReportById(501, 1, "MANAGER");

        assertThat(dto.id()).isEqualTo(501);
        assertThat(dto.farmId()).isEqualTo(1);
    }

    @Test
    void getReportById_admin_bypassesFarmCheckEntirely() {
        when(reportRepository.findById(500)).thenReturn(Optional.of(otherFarmsReport));

        ReportDto dto = reportService.getReportById(500, null, "ADMIN");

        assertThat(dto.id()).isEqualTo(500);
        assertThat(dto.farmId()).isEqualTo(99);
    }

    @Test
    void getReportById_operationsManager_bypassesFarmCheckEntirely() {
        when(reportRepository.findById(500)).thenReturn(Optional.of(otherFarmsReport));

        ReportDto dto = reportService.getReportById(500, 1, "OPERATIONS_MANAGER");

        assertThat(dto.id()).isEqualTo(500);
        assertThat(dto.farmId()).isEqualTo(99);
    }
}
