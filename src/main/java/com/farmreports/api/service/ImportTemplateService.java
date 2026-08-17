package com.farmreports.api.service;

import com.farmreports.api.entity.Employee;
import com.farmreports.api.entity.Farm;
import com.farmreports.api.entity.LivestockCategory;
import com.farmreports.api.entity.LivestockType;
import com.farmreports.api.repository.EmployeeRepository;
import com.farmreports.api.repository.FarmRepository;
import com.farmreports.api.repository.LivestockTypeRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Builds downloadable example .xlsx templates for the four ADMIN bulk importers, generated from
 * the farm/livestock-type/employee data actually in the database so column headers always match
 * what {@link BulkImportService} and {@link EmployeeService} will accept — no hand-maintained
 * static file to drift out of sync with the schema.
 */
@Service
@RequiredArgsConstructor
public class ImportTemplateService {

    private final FarmRepository farmRepo;
    private final LivestockTypeRepository livestockTypeRepo;
    private final EmployeeRepository employeeRepo;

    // Canonical display order per category; mirrors the real client template column order.
    // Types present in the DB but not listed here (e.g. a future addition) are appended
    // alphabetically so the template never silently omits a real column.
    private static final Map<LivestockCategory, List<String>> CANONICAL_TYPE_ORDER = Map.of(
            LivestockCategory.CATTLE, List.of("MILKING", "DRY", "MATERNITY", "WEANERS_HEIFERS",
                    "WEANERS_BULLS", "COMPOUND", "CALVES_HEIFERS", "CALVES_BULLS", "SEGERO"),
            LivestockCategory.PIGS, List.of("BOARS", "SOWS", "PIGLETS", "WEANERS", "PORKERS", "BACONERS"),
            LivestockCategory.SHEEP, List.of("RAMS", "EWES"),
            LivestockCategory.GOATS, List.of("HES", "SHES")
    );

    // Canonical type -> abbreviated header text used by BulkImportService.LIVESTOCK_TYPE_ALIASES.
    private static final Map<String, String> TYPE_HEADER_ALIASES = Map.of(
            "WEANERS_HEIFERS", "WHEIFERS",
            "WEANERS_BULLS", "WBULLS",
            "CALVES_HEIFERS", "CHEIFERS",
            "CALVES_BULLS", "CBULLS"
    );

    // GOATS "TOTALS" is a computed column in the client's workbook, not an importable data column.
    private static final String NO_TEMPLATE_COLUMN_TYPE = "TOTALS";

    // ── Employee import ──────────────────────────────────────────────────────

    public byte[] buildEmployeeTemplate() {
        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            Styles styles = new Styles(wb);
            Sheet sheet = wb.createSheet("employee_import_template");

            String[] headers = {"farmName", "firstName", "lastName", "phone", "employmentType",
                    "jobTitle", "startDate", "dateOfBirth", "nationalId", "gender", "defaultDailyRate"};
            Set<String> required = Set.of("farmName", "firstName", "employmentType");
            writeHeaderRow(sheet, 0, headers, required, styles);

            String farmName = farmRepo.findAll().stream().findFirst().map(Farm::getName).orElse("Matunda");
            writeExampleRow(sheet, 1, styles, farmName, "Jane", "Doe", "0712345678", "SALARIED",
                    "Herdsman", "2024-01-15", "1998-06-20", "12345678", "Female", "");
            writeExampleRow(sheet, 2, styles, farmName, "John", "Kiptoo", "0798765432", "CASUAL",
                    "General labour", "", "", "", "Male", "600");

            for (int i = 0; i < headers.length; i++) sheet.autoSizeColumn(i);
            sheet.createFreezePane(0, 1);

            Sheet notes = wb.createSheet("Instructions");
            writeInstructions(notes, styles, "Employee import (CSV or XLSX)", List.of(
                    "Required columns: farmName, firstName, employmentType.",
                    "farmName must exactly match one of: " + farmNamesList() + ".",
                    "employmentType must be SALARIED or CASUAL.",
                    "startDate and dateOfBirth must be in yyyy-MM-dd format, e.g. 2024-01-15.",
                    "defaultDailyRate is only used for CASUAL employees; leave blank for SALARIED.",
                    "Delete the two example rows on the first sheet before uploading your own data.",
                    "Upload via ADMIN > Bulk Import > Employees. Either .csv or .xlsx is accepted."
            ));

            return toBytes(wb);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not build template");
        }
    }

    // ── Livestock import ─────────────────────────────────────────────────────

    public byte[] buildLivestockTemplate() {
        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            Styles styles = new Styles(wb);
            Sheet sheet = wb.createSheet("livestock_import_template");

            List<Farm> farms = farmRepo.findAll();
            List<LivestockColumn> columns = buildLivestockColumns(farms);

            Row categoryRow = sheet.createRow(0);
            Row typeRow = sheet.createRow(1);
            setCell(categoryRow, 0, "", styles.header);
            setCell(categoryRow, 1, "", styles.header);
            setCell(typeRow, 0, "Month", styles.header);
            setCell(typeRow, 1, "Farm", styles.header);

            for (int i = 0; i < columns.size(); i++) {
                int col = i + 2;
                LivestockColumn c = columns.get(i);
                setCell(categoryRow, col, c.category().name(), styles.header);
                setCell(typeRow, col, TYPE_HEADER_ALIASES.getOrDefault(c.type(), c.type()), styles.header);
            }
            mergeRunsOfEqualValues(sheet, categoryRow.getRowNum(), 2, columns.size());

            Map<Integer, Set<String>> farmColumnKeys = new LinkedHashMap<>();
            for (Farm farm : farms) {
                Set<String> keys = new LinkedHashSet<>();
                for (LivestockType t : livestockTypeRepo.findByFarmId(farm.getId())) {
                    if (!NO_TEMPLATE_COLUMN_TYPE.equals(t.getType())) {
                        keys.add(t.getCategory() + "|" + t.getType());
                    }
                }
                farmColumnKeys.put(farm.getId(), keys);
            }

            int rowNum = 2;
            for (Farm farm : farms) {
                Row row = sheet.createRow(rowNum++);
                setCell(row, 0, "Jan", styles.example);
                setCell(row, 1, farm.getName(), styles.example);
                Set<String> keys = farmColumnKeys.get(farm.getId());
                for (int i = 0; i < columns.size(); i++) {
                    LivestockColumn c = columns.get(i);
                    if (keys.contains(c.category() + "|" + c.type())) {
                        setCell(row, i + 2, 10, styles.example);
                    }
                }
            }

            for (int i = 0; i < columns.size() + 2; i++) sheet.autoSizeColumn(i);
            sheet.createFreezePane(2, 2);

            Sheet notes = wb.createSheet("Instructions");
            writeInstructions(notes, styles, "Livestock import (XLSX only)", List.of(
                    "Two header rows: row 1 groups columns by category (CATTLE/PIGS/SHEEP/GOATS), row 2 has the column name.",
                    "Month must be a 3+ letter month name/abbreviation (e.g. Jan, January).",
                    "Farm must exactly match one of: " + farmNamesList() + ".",
                    "Only the columns that apply to a given farm need values — leave the rest of that row blank " +
                            "(e.g. PIGS columns only apply to Matunda). A filled-in but wrong column for a farm is a row error.",
                    "The example rows below show, per farm, which columns actually apply — delete them before uploading real data.",
                    "You choose the year in the upload dialog; it is not a column in this file.",
                    "Existing data for types/days not included in your file is left untouched (this import merges, it does not overwrite the whole report).",
                    "Upload via ADMIN > Bulk Import > Livestock."
            ));

            return toBytes(wb);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not build template");
        }
    }

    private record LivestockColumn(LivestockCategory category, String type) {}

    private List<LivestockColumn> buildLivestockColumns(List<Farm> farms) {
        Set<String> present = new LinkedHashSet<>();
        for (Farm farm : farms) {
            for (LivestockType t : livestockTypeRepo.findByFarmId(farm.getId())) {
                if (!NO_TEMPLATE_COLUMN_TYPE.equals(t.getType())) {
                    present.add(t.getCategory() + "|" + t.getType());
                }
            }
        }

        List<LivestockColumn> columns = new ArrayList<>();
        for (LivestockCategory category : List.of(LivestockCategory.CATTLE, LivestockCategory.PIGS,
                LivestockCategory.SHEEP, LivestockCategory.GOATS)) {
            List<String> ordered = new ArrayList<>(CANONICAL_TYPE_ORDER.getOrDefault(category, List.of()));
            List<String> extras = present.stream()
                    .filter(key -> key.startsWith(category.name() + "|"))
                    .map(key -> key.substring(category.name().length() + 1))
                    .filter(type -> !ordered.contains(type))
                    .sorted()
                    .toList();
            ordered.addAll(extras);
            for (String type : ordered) {
                if (present.contains(category.name() + "|" + type)) {
                    columns.add(new LivestockColumn(category, type));
                }
            }
        }
        return columns;
    }

    // ── Milk import ──────────────────────────────────────────────────────────

    public byte[] buildMilkTemplate() {
        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            Styles styles = new Styles(wb);
            Sheet sheet = wb.createSheet("milk_import_template");

            Row header = sheet.createRow(0);
            setCell(header, 0, "Month", styles.header);
            setCell(header, 1, "Farm", styles.header);
            for (int day = 1; day <= 31; day++) {
                setCell(header, day + 1, String.valueOf(day), styles.header);
            }

            List<Farm> farms = farmRepo.findAll();
            int[] sampleLitres = {120, 118, 125, 130, 122};
            int rowNum = 1;
            for (Farm farm : farms) {
                Row row = sheet.createRow(rowNum++);
                setCell(row, 0, "Jan", styles.example);
                setCell(row, 1, farm.getName(), styles.example);
                for (int i = 0; i < sampleLitres.length; i++) {
                    setCell(row, i + 2, sampleLitres[i], styles.example);
                }
            }

            for (int i = 0; i < 33; i++) sheet.autoSizeColumn(i);
            sheet.createFreezePane(2, 1);

            Sheet notes = wb.createSheet("Instructions");
            writeInstructions(notes, styles, "Milk production import (XLSX only)", List.of(
                    "One header row: Month, Farm, then a column per day of the month (1-31).",
                    "Month must be a 3+ letter month name/abbreviation (e.g. Jan, January).",
                    "Farm must exactly match one of: " + farmNamesList() + ".",
                    "Leave a day's cell blank if there is no reading for that day — only filled-in days are imported.",
                    "Days beyond the length of the given month (e.g. day 31 in February) must be left blank.",
                    "You choose the year in the upload dialog; it is not a column in this file.",
                    "Existing readings for days not included in your file are left untouched (this import merges).",
                    "Upload via ADMIN > Bulk Import > Milk Production."
            ));

            return toBytes(wb);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not build template");
        }
    }

    // ── Employee pay import ──────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public byte[] buildEmployeePayTemplate() {
        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            Styles styles = new Styles(wb);
            Sheet sheet = wb.createSheet("Employee pay_import");

            List<Employee> employees = employeeRepo.findAllOrderByFarmAndName().stream()
                    .filter(e -> e.getLsNumber() != null && e.getLsNumber().matches("^LS\\d+[A-Z]$"))
                    .toList();

            Row lsRow = sheet.createRow(0);
            Row subHeaderRow = sheet.createRow(1);
            setCell(subHeaderRow, 0, "Month", styles.header);

            int col = 1;
            for (Employee emp : employees) {
                String bareLs = emp.getLsNumber().substring(0, emp.getLsNumber().length() - 1);
                setCell(lsRow, col, bareLs, styles.header);
                setCell(lsRow, col + 1, "", styles.header);
                setCell(subHeaderRow, col, "Earned", styles.header);
                setCell(subHeaderRow, col + 1, "Paid", styles.header);
                sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, col, col + 1));
                col += 2;
            }

            Row example = sheet.createRow(2);
            setCell(example, 0, "Jan", styles.example);
            if (!employees.isEmpty()) {
                setCell(example, 1, 50000, styles.example);
                setCell(example, 2, 50000, styles.example);
            }

            for (int i = 0; i < col; i++) sheet.autoSizeColumn(i);
            sheet.createFreezePane(1, 2);

            Sheet lookup = wb.createSheet("Employee LS Lookup");
            Row lookupHeader = lookup.createRow(0);
            setCell(lookupHeader, 0, "LS Number", styles.header);
            setCell(lookupHeader, 1, "Farm", styles.header);
            setCell(lookupHeader, 2, "Name", styles.header);
            int r = 1;
            for (Employee emp : employees) {
                Row row = lookup.createRow(r++);
                setCell(row, 0, emp.getLsNumber().substring(0, emp.getLsNumber().length() - 1), styles.plain);
                setCell(row, 1, emp.getFarm().getName(), styles.plain);
                setCell(row, 2, emp.getFullName(), styles.plain);
            }
            for (int i = 0; i < 3; i++) lookup.autoSizeColumn(i);

            Sheet notes = wb.createSheet("Instructions");
            writeInstructions(notes, styles, "Employee pay import (XLSX only)", List.of(
                    "Two header rows: row 1 has each employee's bare LS number (no farm-letter suffix), row 2 has Earned/Paid sub-columns.",
                    "The 'Employee LS Lookup' sheet maps each LS number to the employee's name and farm for reference.",
                    "One data row per calendar month, in sequence, starting from the (year, month) you choose in the upload dialog " +
                            "(e.g. if you pick 2026-01, row 1 of data = Jan 2026, row 2 = Feb 2026, and so on).",
                    "The Month label in column A must match the expected month for that row's position — it's a cross-check, not free text.",
                    "Leave both Earned and Paid blank for an employee/month with nothing to import; either can be filled independently.",
                    "'Earned' updates the payroll gross salary; 'Paid' updates both the payroll amount-paid and the employee's payment ledger.",
                    "Re-uploading for the same employee/month replaces the previously imported Paid amount rather than duplicating it.",
                    "Upload via ADMIN > Bulk Import > Employee Pay, selecting the starting year/month for row 1."
            ));

            return toBytes(wb);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not build template");
        }
    }

    // ── Shared helpers ───────────────────────────────────────────────────────

    private String farmNamesList() {
        return farmRepo.findAll().stream().map(Farm::getName).reduce((a, b) -> a + ", " + b).orElse("");
    }

    private void writeHeaderRow(Sheet sheet, int rowNum, String[] headers, Set<String> required, Styles styles) {
        Row row = sheet.createRow(rowNum);
        for (int i = 0; i < headers.length; i++) {
            String text = required.contains(headers[i]) ? headers[i] + " *" : headers[i];
            setCell(row, i, text, styles.header);
        }
    }

    private void writeExampleRow(Sheet sheet, int rowNum, Styles styles, String... values) {
        Row row = sheet.createRow(rowNum);
        for (int i = 0; i < values.length; i++) {
            setCell(row, i, values[i], styles.example);
        }
    }

    private void writeInstructions(Sheet sheet, Styles styles, String title, List<String> lines) {
        Row titleRow = sheet.createRow(0);
        setCell(titleRow, 0, title, styles.title);
        int r = 2;
        for (String line : lines) {
            setCell(sheet.createRow(r++), 0, "- " + line, styles.plain);
        }
        sheet.setColumnWidth(0, 100 * 256);
    }

    private void mergeRunsOfEqualValues(Sheet sheet, int rowNum, int startCol, int count) {
        Row row = sheet.getRow(rowNum);
        int runStart = -1;
        String runValue = null;
        for (int i = 0; i <= count; i++) {
            String value = i < count ? row.getCell(startCol + i).getStringCellValue() : null;
            if (!java.util.Objects.equals(value, runValue) || value == null || value.isBlank()) {
                if (runStart != -1 && runValue != null && !runValue.isBlank() && i - runStart > 1) {
                    sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(
                            rowNum, rowNum, startCol + runStart, startCol + i - 1));
                }
                runStart = i;
                runValue = value;
            }
        }
    }

    private void setCell(Row row, int col, String value, CellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    private void setCell(Row row, int col, double value, CellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    private byte[] toBytes(XSSFWorkbook wb) throws IOException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            wb.write(out);
            return out.toByteArray();
        }
    }

    private static class Styles {
        final CellStyle header;
        final CellStyle example;
        final CellStyle plain;
        final CellStyle title;

        Styles(XSSFWorkbook wb) {
            Font headerFont = wb.createFont();
            headerFont.setBold(true);
            header = wb.createCellStyle();
            header.setFont(headerFont);
            header.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            header.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);

            Font exampleFont = wb.createFont();
            exampleFont.setItalic(true);
            exampleFont.setColor(IndexedColors.GREY_50_PERCENT.getIndex());
            example = wb.createCellStyle();
            example.setFont(exampleFont);

            plain = wb.createCellStyle();

            Font titleFont = wb.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 14);
            title = wb.createCellStyle();
            title.setFont(titleFont);
        }
    }
}
