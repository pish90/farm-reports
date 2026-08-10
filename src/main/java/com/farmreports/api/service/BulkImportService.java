package com.farmreports.api.service;

import com.farmreports.api.dto.ImportResult;
import com.farmreports.api.dto.ImportRowError;
import com.farmreports.api.dto.LivestockEntryRequest;
import com.farmreports.api.dto.MilkEntryRequest;
import com.farmreports.api.dto.ReportDto;
import com.farmreports.api.entity.Employee;
import com.farmreports.api.entity.EmployeePayment;
import com.farmreports.api.entity.Farm;
import com.farmreports.api.entity.LivestockCategory;
import com.farmreports.api.entity.LivestockReturn;
import com.farmreports.api.entity.LivestockType;
import com.farmreports.api.entity.MilkProduction;
import com.farmreports.api.entity.MonthlyReport;
import com.farmreports.api.entity.PayrollEntry;
import com.farmreports.api.repository.EmployeePaymentRepository;
import com.farmreports.api.repository.EmployeeRepository;
import com.farmreports.api.repository.FarmRepository;
import com.farmreports.api.repository.LivestockTypeRepository;
import com.farmreports.api.repository.MonthlyReportRepository;
import com.farmreports.api.repository.PayrollEntryRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Bulk XLSX import for historical livestock and milk production figures, spanning many
 * farms/months in one file. Both importers validate every row first (no partial imports on
 * error) and, for rows that already have report data, merge into the existing counts/days
 * rather than wiping out livestock types or days the template doesn't cover.
 */
@Service
@RequiredArgsConstructor
public class BulkImportService {

    private static final int MAX_IMPORT_ROWS = 2000;

    private final FarmRepository farmRepo;
    private final MonthlyReportRepository reportRepo;
    private final LivestockTypeRepository livestockTypeRepo;
    private final EmployeeRepository employeeRepo;
    private final PayrollEntryRepository payrollRepo;
    private final EmployeePaymentRepository paymentRepo;
    private final ReportService reportService;

    /** Marks employee_payments rows created by the pay importer, so a re-import can find and
     *  replace its own prior rows without touching payments recorded through the normal UI. */
    private static final String PAY_IMPORT_TAG = "Bulk import (Employee pay XLSX)";

    // Template column headers that abbreviate the canonical livestock_types.type value.
    private static final Map<String, String> LIVESTOCK_TYPE_ALIASES = Map.ofEntries(
            Map.entry("WHEIFERS", "WEANERS_HEIFERS"),
            Map.entry("WBULLS", "WEANERS_BULLS"),
            Map.entry("CHEIFERS", "CALVES_HEIFERS"),
            Map.entry("CBULLS", "CALVES_BULLS")
    );

    private static final Map<String, Integer> MONTH_BY_ABBREV = Map.ofEntries(
            Map.entry("jan", 1), Map.entry("feb", 2), Map.entry("mar", 3), Map.entry("apr", 4),
            Map.entry("may", 5), Map.entry("jun", 6), Map.entry("jul", 7), Map.entry("aug", 8),
            Map.entry("sep", 9), Map.entry("oct", 10), Map.entry("nov", 11), Map.entry("dec", 12)
    );

    // ── Livestock ────────────────────────────────────────────────────────────

    @Transactional
    public ImportResult importLivestockFromXlsx(MultipartFile file, Integer year, Integer userId) {
        try (Workbook workbook = openWorkbook(file)) {
            return doImportLivestock(checkedSheet(workbook), year, userId);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error closing XLSX file");
        }
    }

    private ImportResult doImportLivestock(Sheet sheet, Integer year, Integer userId) {
        int headerRow1Num = sheet.getFirstRowNum();
        int headerRow2Num = headerRow1Num + 1;
        Row categoryRow = sheet.getRow(headerRow1Num);
        Row typeRow = sheet.getRow(headerRow2Num);
        if (categoryRow == null || typeRow == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "XLSX file must have two header rows (category group, then type)");
        }

        int lastCol = Math.max(categoryRow.getLastCellNum(), typeRow.getLastCellNum());
        Map<Integer, String> categoryByColumn = new LinkedHashMap<>();
        String currentCategory = null;
        for (int col = 0; col < lastCol; col++) {
            String v = readCellAsString(categoryRow.getCell(col));
            if (v != null) currentCategory = v.trim().toUpperCase();
            if (currentCategory != null) categoryByColumn.put(col, currentCategory);
        }

        Integer monthCol = null, farmCol = null;
        record DataColumn(int col, LivestockCategory category, String type, String header) {}
        List<DataColumn> dataColumns = new ArrayList<>();
        for (int col = 0; col < lastCol; col++) {
            String header = readCellAsString(typeRow.getCell(col));
            if (header == null) continue;
            String normalized = header.trim().toUpperCase();
            if (normalized.equals("MONTH")) {
                monthCol = col;
            } else if (normalized.equals("FARM")) {
                farmCol = col;
            } else {
                LivestockCategory category = parseCategory(categoryByColumn.get(col));
                if (category == null) continue; // no category group above this column — not a data column
                String canonicalType = LIVESTOCK_TYPE_ALIASES.getOrDefault(normalized, normalized);
                dataColumns.add(new DataColumn(col, category, canonicalType, header));
            }
        }
        if (monthCol == null || farmCol == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "XLSX file is missing a 'Month' or 'Farm' column");
        }
        if (dataColumns.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "XLSX file has no recognizable livestock type columns");
        }

        Map<String, Farm> farmsByName = loadFarmsByName();
        Map<Integer, List<LivestockType>> typesByFarm = new LinkedHashMap<>();

        record PreparedRow(Integer farmId, int month, Map<Integer, Integer> countsByTypeId) {}
        List<PreparedRow> prepared = new ArrayList<>();
        List<ImportRowError> errors = new ArrayList<>();
        int dataRowCount = 0;

        for (int r = headerRow2Num + 1; r <= sheet.getLastRowNum(); r++) {
            Row row = sheet.getRow(r);
            if (row == null || isRowBlank(row)) continue;
            dataRowCount++;
            int excelRow = r + 1;
            List<String> issues = new ArrayList<>();

            String monthStr = readCellAsString(row.getCell(monthCol));
            String farmStr = readCellAsString(row.getCell(farmCol));

            Integer month = null;
            if (monthStr == null) {
                issues.add("Month is required");
            } else {
                month = parseMonth(monthStr);
                if (month == null) issues.add("Unrecognized month: '" + monthStr + "'");
            }

            Farm farm = null;
            if (farmStr == null) {
                issues.add("Farm is required");
            } else {
                farm = farmsByName.get(farmStr.trim().toLowerCase());
                if (farm == null) issues.add("Unknown farm: '" + farmStr + "'");
            }

            Map<Integer, Integer> countsByTypeId = new LinkedHashMap<>();
            if (farm != null) {
                List<LivestockType> farmTypes = typesByFarm.computeIfAbsent(
                        farm.getId(), livestockTypeRepo::findByFarmId);

                for (DataColumn dc : dataColumns) {
                    String valueStr = readCellAsString(row.getCell(dc.col()));
                    if (valueStr == null) continue;

                    Integer count = parseNonNegativeInt(valueStr);
                    if (count == null) {
                        issues.add("Invalid count '" + valueStr + "' for " + dc.header());
                        continue;
                    }

                    LivestockType type = farmTypes.stream()
                            .filter(t -> t.getCategory() == dc.category() && t.getType().equalsIgnoreCase(dc.type()))
                            .findFirst().orElse(null);
                    if (type == null) {
                        issues.add("No " + dc.category() + " type '" + dc.type() + "' configured for farm "
                                + farm.getName() + " (column '" + dc.header() + "')");
                        continue;
                    }
                    countsByTypeId.put(type.getId(), count);
                }
            }

            String summary = (farmStr != null ? farmStr : "?") + " / " + (monthStr != null ? monthStr : "?") + " " + year;
            if (!issues.isEmpty()) {
                errors.add(new ImportRowError(excelRow, summary, String.join("; ", issues)));
                continue;
            }
            prepared.add(new PreparedRow(farm.getId(), month, countsByTypeId));
        }

        if (!errors.isEmpty()) {
            return new ImportResult(false, dataRowCount, 0, errors);
        }

        int imported = 0;
        for (PreparedRow row : prepared) {
            ReportDto report = reportService.createOrGetReport(row.farmId(), year, row.month(), userId);
            MonthlyReport entity = reportRepo.findById(report.id())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Report vanished"));

            Map<Integer, Integer> merged = new LinkedHashMap<>();
            for (LivestockReturn lr : entity.getLivestockReturns()) {
                merged.put(lr.getLivestockType().getId(), lr.getCount());
            }
            merged.putAll(row.countsByTypeId());

            List<LivestockEntryRequest> entries = merged.entrySet().stream()
                    .map(e -> new LivestockEntryRequest(e.getKey(), e.getValue()))
                    .toList();
            reportService.upsertLivestock(report.id(), row.farmId(), entries);
            imported++;
        }
        return new ImportResult(true, dataRowCount, imported, List.of());
    }

    // ── Milk ─────────────────────────────────────────────────────────────────

    @Transactional
    public ImportResult importMilkFromXlsx(MultipartFile file, Integer year, Integer userId) {
        try (Workbook workbook = openWorkbook(file)) {
            return doImportMilk(checkedSheet(workbook), year, userId);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error closing XLSX file");
        }
    }

    private ImportResult doImportMilk(Sheet sheet, Integer year, Integer userId) {
        int headerRowNum = sheet.getFirstRowNum();
        Row headerRow = sheet.getRow(headerRowNum);
        if (headerRow == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "XLSX file has no header row");
        }

        Integer monthCol = null, farmCol = null;
        Map<Integer, Integer> dayByColumn = new LinkedHashMap<>();
        int lastCol = headerRow.getLastCellNum();
        for (int col = 0; col < lastCol; col++) {
            String header = readCellAsString(headerRow.getCell(col));
            if (header == null) continue;
            String normalized = header.trim().toUpperCase();
            if (normalized.equals("MONTH")) {
                monthCol = col;
            } else if (normalized.equals("FARM")) {
                farmCol = col;
            } else {
                Integer day = parseNonNegativeInt(header.trim());
                if (day != null && day >= 1 && day <= 31) {
                    dayByColumn.put(col, day);
                }
            }
        }
        if (monthCol == null || farmCol == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "XLSX file is missing a 'Month' or 'Farm' column");
        }
        if (dayByColumn.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "XLSX file has no recognizable day-of-month columns (expected 1-31)");
        }

        Map<String, Farm> farmsByName = loadFarmsByName();

        record PreparedRow(Integer farmId, int month, Map<Integer, BigDecimal> litresByDay) {}
        List<PreparedRow> prepared = new ArrayList<>();
        List<ImportRowError> errors = new ArrayList<>();
        int dataRowCount = 0;

        for (int r = headerRowNum + 1; r <= sheet.getLastRowNum(); r++) {
            Row row = sheet.getRow(r);
            if (row == null || isRowBlank(row)) continue;
            dataRowCount++;
            int excelRow = r + 1;
            List<String> issues = new ArrayList<>();

            String monthStr = readCellAsString(row.getCell(monthCol));
            String farmStr = readCellAsString(row.getCell(farmCol));

            Integer month = null;
            if (monthStr == null) {
                issues.add("Month is required");
            } else {
                month = parseMonth(monthStr);
                if (month == null) issues.add("Unrecognized month: '" + monthStr + "'");
            }

            Farm farm = null;
            if (farmStr == null) {
                issues.add("Farm is required");
            } else {
                farm = farmsByName.get(farmStr.trim().toLowerCase());
                if (farm == null) issues.add("Unknown farm: '" + farmStr + "'");
            }

            Map<Integer, BigDecimal> litresByDay = new LinkedHashMap<>();
            if (month != null) {
                int daysInMonth = YearMonth.of(year, month).lengthOfMonth();
                for (Map.Entry<Integer, Integer> e : dayByColumn.entrySet()) {
                    String valueStr = readCellAsString(row.getCell(e.getKey()));
                    if (valueStr == null) continue;
                    int day = e.getValue();
                    if (day > daysInMonth) {
                        issues.add("Day " + day + " does not exist in " + monthStr + " " + year);
                        continue;
                    }
                    BigDecimal litres = parseNonNegativeDecimal(valueStr);
                    if (litres == null) {
                        issues.add("Invalid litres '" + valueStr + "' for day " + day);
                        continue;
                    }
                    litresByDay.put(day, litres);
                }
            }

            String summary = (farmStr != null ? farmStr : "?") + " / " + (monthStr != null ? monthStr : "?") + " " + year;
            if (!issues.isEmpty()) {
                errors.add(new ImportRowError(excelRow, summary, String.join("; ", issues)));
                continue;
            }
            prepared.add(new PreparedRow(farm.getId(), month, litresByDay));
        }

        if (!errors.isEmpty()) {
            return new ImportResult(false, dataRowCount, 0, errors);
        }

        int imported = 0;
        for (PreparedRow row : prepared) {
            ReportDto report = reportService.createOrGetReport(row.farmId(), year, row.month(), userId);
            MonthlyReport entity = reportRepo.findById(report.id())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Report vanished"));

            Map<Integer, BigDecimal> merged = new LinkedHashMap<>();
            for (MilkProduction mp : entity.getMilkProduction()) {
                merged.put(mp.getDayOfMonth(), mp.getLitres());
            }
            merged.putAll(row.litresByDay());

            List<MilkEntryRequest> entries = merged.entrySet().stream()
                    .map(e -> new MilkEntryRequest(e.getKey(), e.getValue()))
                    .toList();
            reportService.upsertMilk(report.id(), row.farmId(), entries);
            imported++;
        }
        return new ImportResult(true, dataRowCount, imported, List.of());
    }

    // ── Employee pay ─────────────────────────────────────────────────────────

    /**
     * Imports the "Employee pay_import" sheet: one row per calendar month, one Earned/Paid
     * column pair per employee (headed by their bare LS number, e.g. "LS2001" — without the
     * farm-letter suffix employees.ls_number actually carries). The sheet has no year column
     * and month labels repeat across years, so the caller supplies the (year, month) the first
     * data row represents; every subsequent row is assumed to be the next calendar month in
     * sequence, cross-checked against its own Month label.
     *
     * "Earned" is written to payroll_entries.gross_salary and "Paid" to both
     * payroll_entries.amount_paid (read by the Payroll tab) and a tagged employee_payments row
     * (read by the Annual Ledger) — see PAY_IMPORT_TAG. Fields the sheet doesn't provide
     * (salaryRate, daysWorked, loans, notes) are left untouched on existing payroll_entries rows.
     */
    @Transactional
    public ImportResult importEmployeePayFromXlsx(MultipartFile file, Integer startYear, Integer startMonth,
                                                    Integer userId, String paidByName) {
        try (Workbook workbook = openWorkbook(file)) {
            return doImportEmployeePay(checkedSheet(workbook), startYear, startMonth, paidByName);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error closing XLSX file");
        }
    }

    private ImportResult doImportEmployeePay(Sheet sheet, Integer startYear, Integer startMonth, String paidByName) {
        int headerRow1Num = sheet.getFirstRowNum();
        int headerRow2Num = headerRow1Num + 1;
        Row lsNumberRow = sheet.getRow(headerRow1Num);
        Row subHeaderRow = sheet.getRow(headerRow2Num);
        if (lsNumberRow == null || subHeaderRow == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "XLSX file must have two header rows (LS number, then Earned/Paid)");
        }

        int lastCol = Math.max(lsNumberRow.getLastCellNum(), subHeaderRow.getLastCellNum());
        Map<Integer, String> lsNumberByColumn = new LinkedHashMap<>();
        String currentLsNumber = null;
        for (int col = 1; col < lastCol; col++) {
            String v = readCellAsString(lsNumberRow.getCell(col));
            if (v != null) currentLsNumber = v.trim().toUpperCase();
            if (currentLsNumber != null) lsNumberByColumn.put(col, currentLsNumber);
        }

        record EmployeeColumns(String lsNumber, Integer earnedCol, Integer paidCol) {}
        Map<String, int[]> colsByLsNumber = new LinkedHashMap<>(); // [earnedCol, paidCol], -1 = missing
        for (int col = 1; col < lastCol; col++) {
            String sub = readCellAsString(subHeaderRow.getCell(col));
            if (sub == null) continue;
            String normalized = sub.trim().toLowerCase();
            String lsNumber = lsNumberByColumn.get(col);
            if (lsNumber == null) continue; // stray Earned/Paid column with no LS number above it

            int[] pair = colsByLsNumber.computeIfAbsent(lsNumber, k -> new int[]{-1, -1});
            if (normalized.startsWith("earned")) {
                pair[0] = col;
            } else if (normalized.startsWith("paid")) {
                pair[1] = col;
            }
        }
        if (colsByLsNumber.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "XLSX file has no recognizable Earned/Paid employee columns");
        }
        List<EmployeeColumns> employeeColumns = new ArrayList<>();
        for (Map.Entry<String, int[]> e : colsByLsNumber.entrySet()) {
            if (e.getValue()[0] == -1 || e.getValue()[1] == -1) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Employee column '" + e.getKey() + "' is missing its Earned or Paid sub-column");
            }
            employeeColumns.add(new EmployeeColumns(e.getKey(), e.getValue()[0], e.getValue()[1]));
        }

        Map<String, Employee> employeesByLsNumber = employeeRepo.findAll().stream()
                .filter(emp -> emp.getLsNumber() != null && emp.getLsNumber().matches("^LS\\d+[A-Z]$"))
                .collect(Collectors.toMap(
                        emp -> emp.getLsNumber().substring(0, emp.getLsNumber().length() - 1),
                        emp -> emp, (a, b) -> a));

        record PreparedEntry(Integer employeeId, Integer farmId, BigDecimal earned, BigDecimal paid) {}
        record PreparedRow(int year, int month, List<PreparedEntry> entries) {}
        List<PreparedRow> prepared = new ArrayList<>();
        List<ImportRowError> errors = new ArrayList<>();
        int dataRowCount = 0;
        int posIndex = 0;

        for (int r = headerRow2Num + 1; r <= sheet.getLastRowNum(); r++) {
            Row row = sheet.getRow(r);
            if (row == null || isRowBlank(row)) continue;
            dataRowCount++;
            int excelRow = r + 1;
            List<String> issues = new ArrayList<>();

            int expectedMonth = ((startMonth - 1 + posIndex) % 12) + 1;
            int expectedYear = startYear + (startMonth - 1 + posIndex) / 12;
            posIndex++;

            String monthStr = readCellAsString(row.getCell(0));
            if (monthStr == null) {
                issues.add("Month label is required");
            } else {
                Integer parsedMonth = parseMonth(monthStr);
                if (parsedMonth == null) {
                    issues.add("Unrecognized month: '" + monthStr + "'");
                } else if (!parsedMonth.equals(expectedMonth)) {
                    issues.add("Month label '" + monthStr + "' doesn't match the expected "
                            + expectedYear + "-" + String.format("%02d", expectedMonth)
                            + " for this row's position — check the starting year/month or the row order");
                }
            }

            List<PreparedEntry> entries = new ArrayList<>();
            for (EmployeeColumns ec : employeeColumns) {
                String earnedStr = readCellAsString(row.getCell(ec.earnedCol()));
                String paidStr = readCellAsString(row.getCell(ec.paidCol()));
                if (earnedStr == null && paidStr == null) continue;

                Employee employee = employeesByLsNumber.get(ec.lsNumber());
                if (employee == null) {
                    issues.add("Unknown LS number '" + ec.lsNumber() + "'");
                    continue;
                }

                BigDecimal earned = null, paid = null;
                if (earnedStr != null) {
                    earned = parseNonNegativeDecimal(earnedStr);
                    if (earned == null) issues.add("Invalid Earned amount '" + earnedStr + "' for " + ec.lsNumber());
                }
                if (paidStr != null) {
                    paid = parseNonNegativeDecimal(paidStr);
                    if (paid == null) issues.add("Invalid Paid amount '" + paidStr + "' for " + ec.lsNumber());
                }
                if ((earnedStr != null && earned == null) || (paidStr != null && paid == null)) continue;

                entries.add(new PreparedEntry(employee.getId(), employee.getFarm().getId(), earned, paid));
            }

            String summary = (monthStr != null ? monthStr : "?") + " " + expectedYear;
            if (!issues.isEmpty()) {
                errors.add(new ImportRowError(excelRow, summary, String.join("; ", issues)));
                continue;
            }
            prepared.add(new PreparedRow(expectedYear, expectedMonth, entries));
        }

        if (!errors.isEmpty()) {
            return new ImportResult(false, dataRowCount, 0, errors);
        }

        int imported = 0;
        for (PreparedRow row : prepared) {
            for (PreparedEntry entry : row.entries()) {
                upsertPayrollEntry(entry.farmId(), row.year(), row.month(), entry.employeeId(),
                        entry.earned(), entry.paid());
                if (entry.paid() != null) {
                    replaceTaggedPayment(entry.employeeId(), entry.farmId(), row.year(), row.month(),
                            entry.paid(), paidByName);
                }
            }
            imported++;
        }
        return new ImportResult(true, dataRowCount, imported, List.of());
    }

    private void upsertPayrollEntry(Integer farmId, int year, int month, Integer employeeId,
                                     BigDecimal earned, BigDecimal paid) {
        PayrollEntry e = payrollRepo.findByFarmIdAndYearAndMonthAndEmployeeId(farmId, year, month, employeeId)
                .orElseGet(() -> {
                    PayrollEntry fresh = new PayrollEntry();
                    fresh.setFarmId(farmId);
                    fresh.setYear(year);
                    fresh.setMonth(month);
                    fresh.setEmployeeId(employeeId);
                    fresh.setLoans(BigDecimal.ZERO);
                    fresh.setAmountPaid(BigDecimal.ZERO);
                    return fresh;
                });
        if (earned != null) e.setGrossSalary(earned);
        if (paid != null) e.setAmountPaid(paid);

        BigDecimal gross = e.getGrossSalary() != null ? e.getGrossSalary() : BigDecimal.ZERO;
        BigDecimal loans = e.getLoans() != null ? e.getLoans() : BigDecimal.ZERO;
        BigDecimal amountPaid = e.getAmountPaid() != null ? e.getAmountPaid() : BigDecimal.ZERO;
        e.setAmountRemaining(gross.subtract(loans).subtract(amountPaid));
        e.setUpdatedAt(LocalDateTime.now());
        payrollRepo.save(e);
    }

    private void replaceTaggedPayment(Integer employeeId, Integer farmId, int year, int month,
                                       BigDecimal paid, String paidByName) {
        LocalDate monthStart = LocalDate.of(year, month, 1);
        LocalDate monthEnd = monthStart.withDayOfMonth(monthStart.lengthOfMonth());

        List<EmployeePayment> taggedExisting = paymentRepo
                .findByEmployeeIdAndFarmIdAndPaymentDateBetween(employeeId, farmId, monthStart, monthEnd)
                .stream().filter(p -> PAY_IMPORT_TAG.equals(p.getNote())).toList();
        if (!taggedExisting.isEmpty()) paymentRepo.deleteAll(taggedExisting);

        if (paid.signum() > 0) {
            EmployeePayment payment = new EmployeePayment();
            payment.setEmployeeId(employeeId);
            payment.setFarmId(farmId);
            payment.setPaymentDate(monthEnd);
            payment.setAmount(paid);
            payment.setNote(PAY_IMPORT_TAG);
            payment.setPaidBy(paidByName);
            paymentRepo.save(payment);
        }
    }

    // ── Shared helpers ───────────────────────────────────────────────────────

    private Workbook openWorkbook(MultipartFile file) {
        try {
            return WorkbookFactory.create(file.getInputStream());
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not read XLSX file: " + e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not parse XLSX file: " + e.getMessage());
        }
    }

    private Sheet checkedSheet(Workbook workbook) {
        Sheet sheet = workbook.getSheetAt(0);
        if (sheet.getLastRowNum() > MAX_IMPORT_ROWS) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "XLSX exceeds maximum of " + MAX_IMPORT_ROWS + " rows per import");
        }
        return sheet;
    }

    private Map<String, Farm> loadFarmsByName() {
        return farmRepo.findAll().stream()
                .collect(Collectors.toMap(f -> f.getName().trim().toLowerCase(), f -> f, (a, b) -> a));
    }

    private static LivestockCategory parseCategory(String s) {
        if (s == null) return null;
        try {
            return LivestockCategory.valueOf(s.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static Integer parseMonth(String s) {
        String key = s.trim().toLowerCase();
        if (key.length() > 3) key = key.substring(0, 3);
        return MONTH_BY_ABBREV.get(key);
    }

    private static Integer parseNonNegativeInt(String s) {
        try {
            int v = (int) Math.round(Double.parseDouble(s.trim()));
            return v >= 0 ? v : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static BigDecimal parseNonNegativeDecimal(String s) {
        try {
            BigDecimal v = new BigDecimal(s.trim());
            return v.signum() >= 0 ? v : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static boolean isRowBlank(Row row) {
        for (Cell cell : row) {
            if (readCellAsString(cell) != null) return false;
        }
        return true;
    }

    private static String readCellAsString(Cell cell) {
        if (cell == null) return null;
        CellType type = cell.getCellType() == CellType.FORMULA ? cell.getCachedFormulaResultType() : cell.getCellType();
        String value = switch (type) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> {
                double d = cell.getNumericCellValue();
                yield (d == Math.floor(d) && !Double.isInfinite(d))
                        ? String.valueOf((long) d)
                        : String.valueOf(d);
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> null;
        };
        return value != null && !value.isBlank() ? value.trim() : null;
    }
}
