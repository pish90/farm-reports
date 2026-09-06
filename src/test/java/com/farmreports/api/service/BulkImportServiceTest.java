package com.farmreports.api.service;

import com.farmreports.api.dto.ImportResult;
import com.farmreports.api.dto.LivestockEntryRequest;
import com.farmreports.api.dto.MilkEntryRequest;
import com.farmreports.api.dto.ReportDto;
import com.farmreports.api.entity.Employee;
import com.farmreports.api.entity.EmployeePayment;
import com.farmreports.api.entity.Expense;
import com.farmreports.api.entity.ExpenseCategory;
import com.farmreports.api.entity.Farm;
import com.farmreports.api.entity.LivestockCategory;
import com.farmreports.api.entity.LivestockReturn;
import com.farmreports.api.entity.LivestockType;
import com.farmreports.api.entity.MilkProduction;
import com.farmreports.api.entity.MonthlyReport;
import com.farmreports.api.entity.PayrollEntry;
import com.farmreports.api.repository.EmployeePaymentRepository;
import com.farmreports.api.repository.EmployeeRepository;
import com.farmreports.api.repository.ExpenseCategoryRepository;
import com.farmreports.api.repository.ExpenseRepository;
import com.farmreports.api.repository.FarmRepository;
import com.farmreports.api.repository.LivestockTypeRepository;
import com.farmreports.api.repository.MonthlyReportRepository;
import com.farmreports.api.repository.PayrollEntryRepository;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BulkImportServiceTest {

    @Mock FarmRepository farmRepo;
    @Mock MonthlyReportRepository reportRepo;
    @Mock LivestockTypeRepository livestockTypeRepo;
    @Mock EmployeeRepository employeeRepo;
    @Mock PayrollEntryRepository payrollRepo;
    @Mock EmployeePaymentRepository paymentRepo;
    @Mock ReportService reportService;
    @Mock ExpenseRepository expenseRepo;
    @Mock ExpenseCategoryRepository expenseCategoryRepo;

    BulkImportService bulkImportService;

    Farm matunda;

    @BeforeEach
    void setUp() {
        bulkImportService = new BulkImportService(
                farmRepo, reportRepo, livestockTypeRepo, employeeRepo, payrollRepo, paymentRepo, reportService,
                expenseRepo, expenseCategoryRepo);

        matunda = new Farm();
        matunda.setId(1);
        matunda.setName("Matunda");
    }

    private static MockMultipartFile toXlsx(XSSFWorkbook wb, String filename) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        wb.write(out);
        wb.close();
        return new MockMultipartFile("file", filename, "application/vnd.openxmlformats", out.toByteArray());
    }

    private static LivestockType livestockType(int id, LivestockCategory category, String type) {
        LivestockType t = new LivestockType();
        t.setId(id);
        t.setCategory(category);
        t.setType(type);
        return t;
    }

    // ── Livestock ────────────────────────────────────────────────────────────

    @Test
    void importLivestock_validRows_mergesIntoExistingReportAndUpserts() throws IOException {
        XSSFWorkbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("Livestock");
        Row cat = sheet.createRow(0);
        cat.createCell(2).setCellValue("CATTLE");
        cat.createCell(3).setCellValue("CATTLE");
        cat.createCell(4).setCellValue("SHEEP");
        cat.createCell(5).setCellValue("SHEEP");
        Row type = sheet.createRow(1);
        type.createCell(0).setCellValue("Month");
        type.createCell(1).setCellValue("Farm");
        type.createCell(2).setCellValue("MILKING");
        type.createCell(3).setCellValue("DRY");
        type.createCell(4).setCellValue("RAMS");
        type.createCell(5).setCellValue("EWES");
        Row data = sheet.createRow(2);
        data.createCell(0).setCellValue("Jan");
        data.createCell(1).setCellValue("Matunda");
        data.createCell(2).setCellValue(10);
        data.createCell(3).setCellValue(5);
        data.createCell(4).setCellValue(3);
        data.createCell(5).setCellValue(7);
        MockMultipartFile file = toXlsx(wb, "livestock.xlsx");

        when(farmRepo.findAll()).thenReturn(List.of(matunda));
        when(livestockTypeRepo.findByFarmId(1)).thenReturn(List.of(
                livestockType(101, LivestockCategory.CATTLE, "MILKING"),
                livestockType(102, LivestockCategory.CATTLE, "DRY"),
                livestockType(103, LivestockCategory.SHEEP, "RAMS"),
                livestockType(104, LivestockCategory.SHEEP, "EWES")));

        // Existing report already has a GOATS TOTALS entry (id 105) that the template has no column for.
        LivestockType goatsTotals = livestockType(105, LivestockCategory.GOATS, "TOTALS");
        LivestockReturn existing = new LivestockReturn();
        existing.setLivestockType(goatsTotals);
        existing.setCount(20);
        MonthlyReport report = new MonthlyReport();
        report.setId(55);
        report.getLivestockReturns().add(existing);

        when(reportService.createOrGetReport(1, 2026, 1, 99)).thenReturn(
                new ReportDto(55, 1, 2026, 1, "DRAFT", null, null, null, null, null, null));
        when(reportRepo.findById(55)).thenReturn(Optional.of(report));

        ImportResult result = bulkImportService.importLivestockFromXlsx(file, 2026, 99);

        assertThat(result.success()).isTrue();
        assertThat(result.totalRows()).isEqualTo(1);
        assertThat(result.importedCount()).isEqualTo(1);
        assertThat(result.errors()).isEmpty();

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<LivestockEntryRequest>> captor = ArgumentCaptor.forClass(List.class);
        verify(reportService).upsertLivestock(eq(55), eq(1), captor.capture());
        List<LivestockEntryRequest> entries = captor.getValue();
        assertThat(entries).containsExactlyInAnyOrder(
                new LivestockEntryRequest(105, 20), // carried forward untouched
                new LivestockEntryRequest(101, 10),
                new LivestockEntryRequest(102, 5),
                new LivestockEntryRequest(103, 3),
                new LivestockEntryRequest(104, 7));
    }

    @Test
    void importLivestock_unknownFarm_reportsErrorAndWritesNothing() throws IOException {
        XSSFWorkbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("Livestock");
        Row cat = sheet.createRow(0);
        cat.createCell(2).setCellValue("CATTLE");
        Row type = sheet.createRow(1);
        type.createCell(0).setCellValue("Month");
        type.createCell(1).setCellValue("Farm");
        type.createCell(2).setCellValue("MILKING");
        Row data = sheet.createRow(2);
        data.createCell(0).setCellValue("Jan");
        data.createCell(1).setCellValue("Nonexistent Farm");
        data.createCell(2).setCellValue(10);
        MockMultipartFile file = toXlsx(wb, "livestock.xlsx");

        when(farmRepo.findAll()).thenReturn(List.of(matunda));

        ImportResult result = bulkImportService.importLivestockFromXlsx(file, 2026, 99);

        assertThat(result.success()).isFalse();
        assertThat(result.errors()).hasSize(1);
        assertThat(result.errors().get(0).message()).contains("Unknown farm");
        verify(reportService, never()).upsertLivestock(anyInt(), anyInt(), any());
    }

    // ── Milk ─────────────────────────────────────────────────────────────────

    @Test
    void importMilk_validRows_mergesDaysAndSkipsBlankCells() throws IOException {
        XSSFWorkbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("Milk");
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Month");
        header.createCell(1).setCellValue("Farm");
        header.createCell(2).setCellValue("1");
        header.createCell(3).setCellValue("2");
        header.createCell(4).setCellValue("3");
        Row data = sheet.createRow(1);
        data.createCell(0).setCellValue("Jan");
        data.createCell(1).setCellValue("Matunda");
        data.createCell(2).setCellValue(12.5);
        data.createCell(3).setCellValue(13.0);
        // day 3 left blank
        MockMultipartFile file = toXlsx(wb, "milk.xlsx");

        when(farmRepo.findAll()).thenReturn(List.of(matunda));

        MilkProduction existing = new MilkProduction();
        existing.setDayOfMonth(15);
        existing.setLitres(new BigDecimal("9.0"));
        MonthlyReport report = new MonthlyReport();
        report.setId(60);
        report.getMilkProduction().add(existing);

        when(reportService.createOrGetReport(1, 2026, 1, 99)).thenReturn(
                new ReportDto(60, 1, 2026, 1, "DRAFT", null, null, null, null, null, null));
        when(reportRepo.findById(60)).thenReturn(Optional.of(report));

        ImportResult result = bulkImportService.importMilkFromXlsx(file, 2026, 99);

        assertThat(result.success()).isTrue();
        assertThat(result.importedCount()).isEqualTo(1);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<MilkEntryRequest>> captor = ArgumentCaptor.forClass(List.class);
        verify(reportService).upsertMilk(eq(60), eq(1), captor.capture());
        assertThat(captor.getValue()).containsExactlyInAnyOrder(
                new MilkEntryRequest(15, new BigDecimal("9.0")),
                new MilkEntryRequest(1, new BigDecimal("12.5")),
                new MilkEntryRequest(2, new BigDecimal("13"))); // POI reads whole numbers back without trailing zeros
    }

    @Test
    void importMilk_dayBeyondMonthLength_reportsRowError() throws IOException {
        XSSFWorkbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("Milk");
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Month");
        header.createCell(1).setCellValue("Farm");
        header.createCell(2).setCellValue("30");
        Row data = sheet.createRow(1);
        data.createCell(0).setCellValue("Feb"); // 2026 is not a leap year -> 28 days
        data.createCell(1).setCellValue("Matunda");
        data.createCell(2).setCellValue(10);
        MockMultipartFile file = toXlsx(wb, "milk.xlsx");

        when(farmRepo.findAll()).thenReturn(List.of(matunda));

        ImportResult result = bulkImportService.importMilkFromXlsx(file, 2026, 99);

        assertThat(result.success()).isFalse();
        assertThat(result.errors().get(0).message()).contains("Day 30 does not exist");
        verify(reportService, never()).upsertMilk(anyInt(), anyInt(), any());
    }

    // ── Employee pay ─────────────────────────────────────────────────────────

    private static Employee employee(int id, String lsNumber, Farm farm) {
        Employee e = new Employee();
        e.setId(id);
        e.setLsNumber(lsNumber);
        e.setFarm(farm);
        return e;
    }

    @Test
    void importEmployeePay_validRow_writesPayrollAndTaggedPayment() throws IOException {
        XSSFWorkbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("Employee pay_import");
        Row lsRow = sheet.createRow(0);
        lsRow.createCell(1).setCellValue("LS2001");
        lsRow.createCell(2).setCellValue("LS2001");
        Row subRow = sheet.createRow(1);
        subRow.createCell(1).setCellValue("Earned");
        subRow.createCell(2).setCellValue("Paid");
        Row data = sheet.createRow(2);
        data.createCell(0).setCellValue("Jan");
        data.createCell(1).setCellValue(50000);
        data.createCell(2).setCellValue(20000);
        MockMultipartFile file = toXlsx(wb, "pay.xlsx");

        Employee jane = employee(7, "LS2001M", matunda);
        when(employeeRepo.findAll()).thenReturn(List.of(jane));
        when(payrollRepo.findByFarmIdAndYearAndMonthAndEmployeeId(1, 2026, 1, 7)).thenReturn(Optional.empty());
        when(paymentRepo.findByEmployeeIdAndFarmIdAndPaymentDateBetween(eq(7), eq(1), any(), any()))
                .thenReturn(List.of());

        ImportResult result = bulkImportService.importEmployeePayFromXlsx(file, 2026, 1, 42, "Peter Khayundi");

        assertThat(result.success()).isTrue();
        assertThat(result.importedCount()).isEqualTo(1);

        ArgumentCaptor<PayrollEntry> entryCaptor = ArgumentCaptor.forClass(PayrollEntry.class);
        verify(payrollRepo).save(entryCaptor.capture());
        PayrollEntry saved = entryCaptor.getValue();
        assertThat(saved.getGrossSalary()).isEqualByComparingTo("50000");
        assertThat(saved.getAmountPaid()).isEqualByComparingTo("20000");
        assertThat(saved.getAmountRemaining()).isEqualByComparingTo("30000"); // gross - loans(0) - paid

        ArgumentCaptor<EmployeePayment> paymentCaptor = ArgumentCaptor.forClass(EmployeePayment.class);
        verify(paymentRepo).save(paymentCaptor.capture());
        EmployeePayment payment = paymentCaptor.getValue();
        assertThat(payment.getEmployeeId()).isEqualTo(7);
        assertThat(payment.getAmount()).isEqualByComparingTo("20000");
        assertThat(payment.getNote()).isEqualTo("Bulk import (Employee pay XLSX)");
        assertThat(payment.getPaidBy()).isEqualTo("Peter Khayundi");
    }

    @Test
    void importEmployeePay_monthLabelMismatch_reportsRowError() throws IOException {
        XSSFWorkbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("Employee pay_import");
        Row lsRow = sheet.createRow(0);
        lsRow.createCell(1).setCellValue("LS2001");
        lsRow.createCell(2).setCellValue("LS2001");
        Row subRow = sheet.createRow(1);
        subRow.createCell(1).setCellValue("Earned");
        subRow.createCell(2).setCellValue("Paid");
        Row data = sheet.createRow(2);
        data.createCell(0).setCellValue("Feb"); // expected Jan for startMonth=1, first row
        data.createCell(1).setCellValue(50000);
        data.createCell(2).setCellValue(20000);
        MockMultipartFile file = toXlsx(wb, "pay.xlsx");

        Employee jane = employee(7, "LS2001M", matunda);
        when(employeeRepo.findAll()).thenReturn(List.of(jane));

        ImportResult result = bulkImportService.importEmployeePayFromXlsx(file, 2026, 1, 42, "Peter Khayundi");

        assertThat(result.success()).isFalse();
        assertThat(result.errors().get(0).message()).contains("doesn't match the expected 2026-01");
        verify(payrollRepo, never()).save(any());
        verify(paymentRepo, never()).save(any());
    }

    @Test
    void importEmployeePay_unknownLsNumber_reportsRowError() throws IOException {
        XSSFWorkbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("Employee pay_import");
        Row lsRow = sheet.createRow(0);
        lsRow.createCell(1).setCellValue("LS9999");
        lsRow.createCell(2).setCellValue("LS9999");
        Row subRow = sheet.createRow(1);
        subRow.createCell(1).setCellValue("Earned");
        subRow.createCell(2).setCellValue("Paid");
        Row data = sheet.createRow(2);
        data.createCell(0).setCellValue("Jan");
        data.createCell(1).setCellValue(50000);
        data.createCell(2).setCellValue(20000);
        MockMultipartFile file = toXlsx(wb, "pay.xlsx");

        when(employeeRepo.findAll()).thenReturn(List.of());

        ImportResult result = bulkImportService.importEmployeePayFromXlsx(file, 2026, 1, 42, "Peter Khayundi");

        assertThat(result.success()).isFalse();
        assertThat(result.errors().get(0).message()).contains("Unknown LS number 'LS9999'");
    }

    // ── Expenses ─────────────────────────────────────────────────────────────

    private static ExpenseCategory category(int id, String code, String name) {
        ExpenseCategory c = new ExpenseCategory();
        c.setId(id);
        c.setAccountCode(code);
        c.setAccountName(name);
        return c;
    }

    private static MockMultipartFile expensesCsv(String csv) {
        return new MockMultipartFile("file", "expenses.csv", "text/csv", csv.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    void importExpensesFromCsv_validRows_appendsToExistingReportWithIncrementingEntryNo() {
        when(farmRepo.findAll()).thenReturn(List.of(matunda));
        when(expenseCategoryRepo.findAll()).thenReturn(List.of(category(1, "4000", "Fuel")));
        when(expenseRepo.existsByReport_Farm_IdAndReceiptNoIgnoreCase(any(), any())).thenReturn(false);
        when(expenseRepo.findMaxEntryNoByReportId(55)).thenReturn(3);
        when(reportService.createOrGetReport(1, 2026, 1, 42)).thenReturn(
                new ReportDto(55, 1, 2026, 1, "DRAFT", null, null, null, null, null, null));
        when(reportRepo.getReferenceById(55)).thenReturn(new MonthlyReport());

        String csv = "farm,date,ID,supplier,product/service,category,amount\n"
                + "Matunda,2026-01-15,INV-1001,ABC Traders,Diesel,Fuel,5400.00\n"
                + "matunda,2026-01-18,INV-1002,XYZ Ltd,Tyres,fuel,1200\n";

        ImportResult result = bulkImportService.importExpensesFromCsv(expensesCsv(csv), 42);

        assertThat(result.success()).isTrue();
        assertThat(result.totalRows()).isEqualTo(2);
        assertThat(result.importedCount()).isEqualTo(2);
        assertThat(result.errors()).isEmpty();

        ArgumentCaptor<Expense> captor = ArgumentCaptor.forClass(Expense.class);
        verify(expenseRepo, org.mockito.Mockito.times(2)).save(captor.capture());
        List<Expense> saved = captor.getAllValues();
        assertThat(saved.get(0).getEntryNo()).isEqualTo(4);
        assertThat(saved.get(0).getReceiptNo()).isEqualTo("INV-1001");
        assertThat(saved.get(0).getCost()).isEqualByComparingTo("5400.00");
        assertThat(saved.get(0).getCategory().getAccountName()).isEqualTo("Fuel");
        assertThat(saved.get(1).getEntryNo()).isEqualTo(5);
        assertThat(saved.get(1).getReceiptNo()).isEqualTo("INV-1002");
    }

    @Test
    void importExpensesFromCsv_duplicateIdAlreadyInDb_rejectsRowAndWritesNothing() {
        when(farmRepo.findAll()).thenReturn(List.of(matunda));
        when(expenseCategoryRepo.findAll()).thenReturn(List.of());
        when(expenseRepo.existsByReport_Farm_IdAndReceiptNoIgnoreCase(1, "INV-1001")).thenReturn(true);

        String csv = "farm,date,ID,supplier,product/service,category,amount\n"
                + "Matunda,2026-01-15,INV-1001,ABC Traders,Diesel,,5400.00\n";

        ImportResult result = bulkImportService.importExpensesFromCsv(expensesCsv(csv), 42);

        assertThat(result.success()).isFalse();
        assertThat(result.errors()).hasSize(1);
        assertThat(result.errors().get(0).message()).contains("already exists");
        verify(expenseRepo, never()).save(any());
    }

    @Test
    void importExpensesFromCsv_duplicateIdWithinFile_rejectsSecondOccurrence() {
        when(farmRepo.findAll()).thenReturn(List.of(matunda));
        when(expenseCategoryRepo.findAll()).thenReturn(List.of());
        when(expenseRepo.existsByReport_Farm_IdAndReceiptNoIgnoreCase(any(), any())).thenReturn(false);

        String csv = "farm,date,ID,supplier,product/service,category,amount\n"
                + "Matunda,2026-01-15,INV-1001,ABC Traders,Diesel,,5400.00\n"
                + "Matunda,2026-01-16,inv-1001,ABC Traders,Diesel,,900\n";

        ImportResult result = bulkImportService.importExpensesFromCsv(expensesCsv(csv), 42);

        assertThat(result.success()).isFalse();
        assertThat(result.errors()).hasSize(1);
        assertThat(result.errors().get(0).row()).isEqualTo(3);
        assertThat(result.errors().get(0).message()).contains("Duplicate ID in file");
        verify(expenseRepo, never()).save(any());
    }

    @Test
    void importExpensesFromCsv_unrecognizedCategory_leftBlankNotAnError() {
        when(farmRepo.findAll()).thenReturn(List.of(matunda));
        when(expenseCategoryRepo.findAll()).thenReturn(List.of(category(1, "4000", "Fuel")));
        when(expenseRepo.existsByReport_Farm_IdAndReceiptNoIgnoreCase(any(), any())).thenReturn(false);
        when(expenseRepo.findMaxEntryNoByReportId(55)).thenReturn(0);
        when(reportService.createOrGetReport(1, 2026, 1, 42)).thenReturn(
                new ReportDto(55, 1, 2026, 1, "DRAFT", null, null, null, null, null, null));
        when(reportRepo.getReferenceById(55)).thenReturn(new MonthlyReport());

        String csv = "farm,date,ID,supplier,product/service,category,amount\n"
                + "Matunda,2026-01-15,INV-1001,ABC Traders,Diesel,Nonexistent Category,5400.00\n";

        ImportResult result = bulkImportService.importExpensesFromCsv(expensesCsv(csv), 42);

        assertThat(result.success()).isTrue();
        ArgumentCaptor<Expense> captor = ArgumentCaptor.forClass(Expense.class);
        verify(expenseRepo).save(captor.capture());
        assertThat(captor.getValue().getCategory()).isNull();
    }

    @Test
    void importExpensesFromCsv_missingRequiredColumn_throwsBadRequest() {
        String csv = "farm,date,supplier,amount\n" // missing ID column
                + "Matunda,2026-01-15,ABC Traders,5400.00\n";

        org.assertj.core.api.Assertions.assertThatThrownBy(
                        () -> bulkImportService.importExpensesFromCsv(expensesCsv(csv), 42))
                .hasMessageContaining("missing required column");
    }
}
