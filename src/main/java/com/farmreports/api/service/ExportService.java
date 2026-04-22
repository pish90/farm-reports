package com.farmreports.api.service;

import com.farmreports.api.dto.*;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExportService {

    private static final String[] MONTH_NAMES = {
        "", "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    };
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public byte[] generateExcel(ReportDto report) {
        try (XSSFWorkbook wb = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            CellStyle headerStyle = createHeaderStyle(wb);
            CellStyle titleStyle  = createTitleStyle(wb);
            CellStyle numStyle    = createNumStyle(wb);
            CellStyle boldStyle   = createBoldStyle(wb);

            buildAttendanceSheet(wb, report, headerStyle, titleStyle, numStyle, boldStyle);
            buildLivestockSheet(wb, report, headerStyle, titleStyle, numStyle, boldStyle);
            buildMilkSheet(wb, report, headerStyle, titleStyle, numStyle, boldStyle);
            buildExpensesSheet(wb, report, headerStyle, titleStyle, numStyle, boldStyle);

            wb.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate Excel report", e);
        }
    }

    // ── Attendance ─────────────────────────────────────────────────────────────

    private void buildAttendanceSheet(XSSFWorkbook wb, ReportDto report,
            CellStyle hdr, CellStyle title, CellStyle num, CellStyle bold) {
        Sheet sheet = wb.createSheet("Attendance");
        int days = YearMonth.of(report.year(), report.month()).lengthOfMonth();
        List<AttendanceRecordDto> records = report.attendance() != null ? report.attendance() : List.of();

        // Title row
        Row titleRow = sheet.createRow(0);
        Cell tc = titleRow.createCell(0);
        tc.setCellValue("Attendance – " + MONTH_NAMES[report.month()] + " " + report.year());
        tc.setCellStyle(title);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, days + 2));

        // Header row
        Row headerRow = sheet.createRow(1);
        createHdrCell(headerRow, 0, "Worker", hdr);
        for (int d = 1; d <= days; d++) createHdrCell(headerRow, d, String.valueOf(d), hdr);
        createHdrCell(headerRow, days + 1, "Present", hdr);
        createHdrCell(headerRow, days + 2, "Days", hdr);

        // Group by worker name, preserving insertion order
        Map<String, Map<Integer, Boolean>> grid = new LinkedHashMap<>();
        for (AttendanceRecordDto r : records) {
            grid.computeIfAbsent(r.workerName(), k -> new HashMap<>()).put(r.dayOfMonth(), r.present());
        }

        int rowIdx = 2;
        int[] dayTotals = new int[days + 1]; // 1-based
        for (Map.Entry<String, Map<Integer, Boolean>> e : grid.entrySet()) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(e.getKey());
            int presentCount = 0;
            for (int d = 1; d <= days; d++) {
                Boolean present = e.getValue().get(d);
                Cell c = row.createCell(d);
                if (Boolean.TRUE.equals(present)) {
                    c.setCellValue("✓");
                    presentCount++;
                    dayTotals[d]++;
                }
            }
            Cell pc = row.createCell(days + 1);
            pc.setCellValue(presentCount);
            pc.setCellStyle(num);
            Cell dc = row.createCell(days + 2);
            dc.setCellValue(days);
            dc.setCellStyle(num);
        }

        // Totals row
        Row totalRow = sheet.createRow(rowIdx);
        Cell tc2 = totalRow.createCell(0);
        tc2.setCellValue("TOTAL");
        tc2.setCellStyle(bold);
        int grandTotal = 0;
        for (int d = 1; d <= days; d++) {
            Cell c = totalRow.createCell(d);
            c.setCellValue(dayTotals[d]);
            c.setCellStyle(num);
            grandTotal += dayTotals[d];
        }
        Cell gt = totalRow.createCell(days + 1);
        gt.setCellValue(grandTotal);
        gt.setCellStyle(bold);

        sheet.setColumnWidth(0, 6000);
        for (int d = 1; d <= days; d++) sheet.setColumnWidth(d, 1200);
        sheet.setColumnWidth(days + 1, 2000);
        sheet.setColumnWidth(days + 2, 2000);
    }

    // ── Livestock ──────────────────────────────────────────────────────────────

    private void buildLivestockSheet(XSSFWorkbook wb, ReportDto report,
            CellStyle hdr, CellStyle title, CellStyle num, CellStyle bold) {
        Sheet sheet = wb.createSheet("Livestock Returns");
        List<LivestockRecordDto> records = report.livestock() != null ? report.livestock() : List.of();

        Row titleRow = sheet.createRow(0);
        Cell tc = titleRow.createCell(0);
        tc.setCellValue("Livestock Returns – " + MONTH_NAMES[report.month()] + " " + report.year());
        tc.setCellStyle(title);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 2));

        Row headerRow = sheet.createRow(1);
        createHdrCell(headerRow, 0, "Category", hdr);
        createHdrCell(headerRow, 1, "Type", hdr);
        createHdrCell(headerRow, 2, "Count", hdr);

        Map<String, List<LivestockRecordDto>> byCategory = records.stream()
            .collect(Collectors.groupingBy(LivestockRecordDto::category, LinkedHashMap::new, Collectors.toList()));

        int rowIdx = 2;
        int grandTotal = 0;
        for (Map.Entry<String, List<LivestockRecordDto>> e : byCategory.entrySet()) {
            int catTotal = 0;
            for (LivestockRecordDto lr : e.getValue()) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(toTitle(e.getKey()));
                row.createCell(1).setCellValue(lr.type());
                Cell cc = row.createCell(2);
                cc.setCellValue(lr.count());
                cc.setCellStyle(num);
                catTotal += lr.count();
            }
            Row subRow = sheet.createRow(rowIdx++);
            Cell sc = subRow.createCell(1);
            sc.setCellValue(toTitle(e.getKey()) + " Total");
            sc.setCellStyle(bold);
            Cell sv = subRow.createCell(2);
            sv.setCellValue(catTotal);
            sv.setCellStyle(bold);
            grandTotal += catTotal;
        }

        Row grandRow = sheet.createRow(rowIdx);
        Cell gc = grandRow.createCell(1);
        gc.setCellValue("GRAND TOTAL");
        gc.setCellStyle(bold);
        Cell gv = grandRow.createCell(2);
        gv.setCellValue(grandTotal);
        gv.setCellStyle(bold);

        sheet.setColumnWidth(0, 4000);
        sheet.setColumnWidth(1, 5000);
        sheet.setColumnWidth(2, 3000);
    }

    // ── Milk ───────────────────────────────────────────────────────────────────

    private void buildMilkSheet(XSSFWorkbook wb, ReportDto report,
            CellStyle hdr, CellStyle title, CellStyle num, CellStyle bold) {
        Sheet sheet = wb.createSheet("Milk Production");
        List<MilkRecordDto> records = report.milk() != null
            ? report.milk().stream().sorted(Comparator.comparingInt(MilkRecordDto::dayOfMonth)).toList()
            : List.of();

        Row titleRow = sheet.createRow(0);
        Cell tc = titleRow.createCell(0);
        tc.setCellValue("Milk Production – " + MONTH_NAMES[report.month()] + " " + report.year());
        tc.setCellStyle(title);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));

        Row headerRow = sheet.createRow(1);
        createHdrCell(headerRow, 0, "Day",           hdr);
        createHdrCell(headerRow, 1, "Date",          hdr);
        createHdrCell(headerRow, 2, "Litres",        hdr);
        createHdrCell(headerRow, 3, "Running Total", hdr);

        double running = 0;
        int rowIdx = 2;
        for (MilkRecordDto m : records) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(m.dayOfMonth());
            LocalDate date = LocalDate.of(report.year(), report.month(), m.dayOfMonth());
            row.createCell(1).setCellValue(date.format(DATE_FMT));
            double litres = m.litres() != null ? m.litres().doubleValue() : 0;
            Cell lc = row.createCell(2);
            lc.setCellValue(litres);
            lc.setCellStyle(num);
            running += litres;
            Cell rc = row.createCell(3);
            rc.setCellValue(running);
            rc.setCellStyle(num);
        }

        rowIdx++; // blank row
        Row totalRow = sheet.createRow(rowIdx++);
        Cell tl = totalRow.createCell(2);
        tl.setCellValue("Total Litres");
        tl.setCellStyle(bold);
        Cell tv = totalRow.createCell(3);
        tv.setCellValue(running);
        tv.setCellStyle(bold);

        Row valueRow = sheet.createRow(rowIdx);
        Cell vl = valueRow.createCell(2);
        vl.setCellValue("Value (×40)");
        vl.setCellStyle(bold);
        Cell vv = valueRow.createCell(3);
        vv.setCellValue(running * 40);
        vv.setCellStyle(bold);

        sheet.setColumnWidth(0, 2000);
        sheet.setColumnWidth(1, 3500);
        sheet.setColumnWidth(2, 3500);
        sheet.setColumnWidth(3, 3500);
    }

    // ── Expenses ───────────────────────────────────────────────────────────────

    private void buildExpensesSheet(XSSFWorkbook wb, ReportDto report,
            CellStyle hdr, CellStyle title, CellStyle num, CellStyle bold) {
        Sheet sheet = wb.createSheet("Expenses");
        List<ExpenseRecordDto> records = report.expenses() != null
            ? report.expenses().stream().sorted(Comparator.comparingInt(ExpenseRecordDto::entryNo)).toList()
            : List.of();

        Row titleRow = sheet.createRow(0);
        Cell tc = titleRow.createCell(0);
        tc.setCellValue("Expenses – " + MONTH_NAMES[report.month()] + " " + report.year());
        tc.setCellStyle(title);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 4));

        Row headerRow = sheet.createRow(1);
        createHdrCell(headerRow, 0, "No.",                   hdr);
        createHdrCell(headerRow, 1, "Date",                  hdr);
        createHdrCell(headerRow, 2, "Supplier / Contractor", hdr);
        createHdrCell(headerRow, 3, "Ref No",                hdr);
        createHdrCell(headerRow, 4, "Cost",                  hdr);

        double total = 0;
        int rowIdx = 2;
        for (ExpenseRecordDto e : records) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(e.entryNo());
            row.createCell(1).setCellValue(e.date() != null ? e.date().format(DATE_FMT) : "");
            row.createCell(2).setCellValue(e.supplierContractor() != null ? e.supplierContractor() : "");
            row.createCell(3).setCellValue(e.receiptNo() != null ? e.receiptNo() : "");
            double cost = e.cost() != null ? e.cost().doubleValue() : 0;
            Cell cc = row.createCell(4);
            cc.setCellValue(cost);
            cc.setCellStyle(num);
            total += cost;
        }

        Row totalRow = sheet.createRow(rowIdx);
        Cell tl = totalRow.createCell(3);
        tl.setCellValue("TOTAL");
        tl.setCellStyle(bold);
        Cell tv = totalRow.createCell(4);
        tv.setCellValue(total);
        tv.setCellStyle(bold);

        sheet.setColumnWidth(0, 2000);
        sheet.setColumnWidth(1, 3500);
        sheet.setColumnWidth(2, 8000);
        sheet.setColumnWidth(3, 4000);
        sheet.setColumnWidth(4, 3500);
    }

    // ── Style helpers ──────────────────────────────────────────────────────────

    private CellStyle createHeaderStyle(XSSFWorkbook wb) {
        XSSFCellStyle s = wb.createCellStyle();
        XSSFColor green = new XSSFColor(new byte[]{(byte) 45, (byte) 106, (byte) 79}, null);
        s.setFillForegroundColor(green);
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        XSSFFont f = wb.createFont();
        f.setBold(true);
        f.setColor(new XSSFColor(new byte[]{(byte) 255, (byte) 255, (byte) 255}, null));
        s.setFont(f);
        s.setAlignment(HorizontalAlignment.CENTER);
        setBorder(s);
        return s;
    }

    private CellStyle createTitleStyle(XSSFWorkbook wb) {
        CellStyle s = wb.createCellStyle();
        Font f = wb.createFont();
        f.setBold(true);
        f.setFontHeightInPoints((short) 13);
        s.setFont(f);
        return s;
    }

    private CellStyle createNumStyle(XSSFWorkbook wb) {
        CellStyle s = wb.createCellStyle();
        s.setDataFormat(wb.createDataFormat().getFormat("0.00"));
        s.setAlignment(HorizontalAlignment.RIGHT);
        setBorder(s);
        return s;
    }

    private CellStyle createBoldStyle(XSSFWorkbook wb) {
        CellStyle s = wb.createCellStyle();
        Font f = wb.createFont();
        f.setBold(true);
        s.setFont(f);
        return s;
    }

    private void setBorder(CellStyle s) {
        s.setBorderBottom(BorderStyle.THIN);
        s.setBorderTop(BorderStyle.THIN);
        s.setBorderLeft(BorderStyle.THIN);
        s.setBorderRight(BorderStyle.THIN);
    }

    private void createHdrCell(Row row, int col, String value, CellStyle style) {
        Cell c = row.createCell(col);
        c.setCellValue(value);
        c.setCellStyle(style);
    }

    private String toTitle(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.charAt(0) + s.substring(1).toLowerCase();
    }
}
