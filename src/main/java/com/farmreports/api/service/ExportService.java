package com.farmreports.api.service;

import com.farmreports.api.dto.*;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
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

    public record FarmReport(String farmName, ReportDto report) {}

    public record CasualLabourerRow(String name, String phone, BigDecimal monthEarnings,
                                    BigDecimal totalPaid, BigDecimal outstanding) {}
    public record CasualWorkLogRow(String labourerName, int day, int year, int month,
                                   String taskDescription, BigDecimal rate, BigDecimal amount) {}
    public record CasualPaymentRow(String labourerName, java.time.LocalDate date,
                                   BigDecimal amount, String note, String paidBy) {}

    private record Styles(CellStyle header, CellStyle title, CellStyle num, CellStyle bold, CellStyle center) {}

    // ── Single-farm export ─────────────────────────────────────────────────────

    public byte[] generateExcel(ReportDto report) {
        try (XSSFWorkbook wb = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Styles s = createStyles(wb);
            int days = YearMonth.of(report.year(), report.month()).lengthOfMonth();

            Sheet attSheet = wb.createSheet("Attendance");
            buildAttendanceSection(attSheet, report, 0, s);
            setAttendanceColumnWidths(attSheet, days);

            Sheet liveSheet = wb.createSheet("Livestock Returns");
            buildLivestockSection(liveSheet, report, 0, s);
            liveSheet.setColumnWidth(0, 4000);
            liveSheet.setColumnWidth(1, 5000);
            liveSheet.setColumnWidth(2, 3000);

            Sheet milkSheet = wb.createSheet("Milk Production");
            buildMilkSection(milkSheet, report, 0, s);
            milkSheet.setColumnWidth(0, 2000);
            milkSheet.setColumnWidth(1, 3500);
            milkSheet.setColumnWidth(2, 3500);
            milkSheet.setColumnWidth(3, 3500);

            Sheet expSheet = wb.createSheet("Expenses");
            buildExpensesSection(expSheet, report, 0, s);
            expSheet.setColumnWidth(0, 2000);
            expSheet.setColumnWidth(1, 3500);
            expSheet.setColumnWidth(2, 8000);
            expSheet.setColumnWidth(3, 4000);
            expSheet.setColumnWidth(4, 3500);

            Sheet casualSheet = wb.createSheet("Casual Labourers");
            buildCasualAttendanceSection(casualSheet, report, 0, s);
            setAttendanceColumnWidths(casualSheet, days);
            casualSheet.setColumnWidth(days + 7, 4000); // Amount Due column

            wb.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate Excel report", e);
        }
    }

    // ── Multi-farm export (one sheet per farm, all sections stacked) ───────────

    public byte[] generateAllFarmsExcel(int year, int month, List<FarmReport> farmReports) {
        try (XSSFWorkbook wb = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Styles s = createStyles(wb);
            int days = YearMonth.of(year, month).lengthOfMonth();

            for (FarmReport fr : farmReports) {
                String raw = fr.farmName() != null ? fr.farmName() : "Farm " + fr.report().farmId();
                String sheetName = raw.length() > 31 ? raw.substring(0, 31) : raw;
                Sheet sheet = wb.createSheet(sheetName);

                int row = 0;
                row = buildAttendanceSection(sheet, fr.report(), row, s);
                row += 2;
                row = buildLivestockSection(sheet, fr.report(), row, s);
                row += 2;
                row = buildMilkSection(sheet, fr.report(), row, s);
                row += 2;
                row = buildExpensesSection(sheet, fr.report(), row, s);
                row += 2;
                buildCasualAttendanceSection(sheet, fr.report(), row, s);

                // Column widths tuned for the attendance grid
                sheet.setColumnWidth(0, 6000);
                for (int d = 1; d <= Math.min(4, days); d++) sheet.setColumnWidth(d, 3000);
                for (int d = 5; d <= days; d++) sheet.setColumnWidth(d, 1200);
                for (int i = 1; i <= 6; i++) sheet.setColumnWidth(days + i, 2000);
            }

            wb.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate Excel report", e);
        }
    }

    // ── Section builders (return next available row index) ─────────────────────

    private int buildAttendanceSection(Sheet sheet, ReportDto report, int startRow, Styles s) {
        int days = YearMonth.of(report.year(), report.month()).lengthOfMonth();
        List<AttendanceRecordDto> records = report.attendance() != null ? report.attendance() : List.of();

        Row titleRow = sheet.createRow(startRow);
        Cell tc = titleRow.createCell(0);
        tc.setCellValue("Attendance – " + MONTH_NAMES[report.month()] + " " + report.year());
        tc.setCellStyle(s.title());
        sheet.addMergedRegion(new CellRangeAddress(startRow, startRow, 0, days + 6));

        Row headerRow = sheet.createRow(startRow + 1);
        createHdrCell(headerRow, 0, "Worker", s.header());
        for (int d = 1; d <= days; d++) createHdrCell(headerRow, d, String.valueOf(d), s.header());
        createHdrCell(headerRow, days + 1, "Present", s.header());
        createHdrCell(headerRow, days + 2, "Absent",  s.header());
        createHdrCell(headerRow, days + 3, "Annual",  s.header());
        createHdrCell(headerRow, days + 4, "Sick",    s.header());
        createHdrCell(headerRow, days + 5, "Parent",  s.header());
        createHdrCell(headerRow, days + 6, "Days",    s.header());

        Map<String, Map<Integer, String>> grid = new LinkedHashMap<>();
        for (AttendanceRecordDto r : records) {
            String status = r.status() != null ? r.status() : (r.present() ? "P" : "A");
            grid.computeIfAbsent(r.workerName(), k -> new HashMap<>()).put(r.dayOfMonth(), status);
        }

        int rowIdx = startRow + 2;
        int[] dayPresent = new int[days + 1];
        for (Map.Entry<String, Map<Integer, String>> e : grid.entrySet()) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(e.getKey());
            int cntP = 0, cntA = 0, cntAL = 0, cntSL = 0, cntPL = 0;
            for (int d = 1; d <= days; d++) {
                String status = e.getValue().getOrDefault(d, "A");
                Cell c = row.createCell(d);
                c.setCellValue(status);
                c.setCellStyle(s.center());
                switch (status) {
                    case "P"  -> { cntP++;  dayPresent[d]++; }
                    case "A"  -> cntA++;
                    case "AL" -> cntAL++;
                    case "SL" -> cntSL++;
                    case "PL" -> cntPL++;
                }
            }
            numCell(row, days + 1, cntP,  s);
            numCell(row, days + 2, cntA,  s);
            numCell(row, days + 3, cntAL, s);
            numCell(row, days + 4, cntSL, s);
            numCell(row, days + 5, cntPL, s);
            numCell(row, days + 6, days,  s);
        }

        Row totalRow = sheet.createRow(rowIdx++);
        Cell tc2 = totalRow.createCell(0);
        tc2.setCellValue("TOTAL");
        tc2.setCellStyle(s.bold());
        int grandPresent = 0;
        for (int d = 1; d <= days; d++) {
            Cell c = totalRow.createCell(d);
            c.setCellValue(dayPresent[d]);
            c.setCellStyle(s.num());
            grandPresent += dayPresent[d];
        }
        Cell gt = totalRow.createCell(days + 1);
        gt.setCellValue(grandPresent);
        gt.setCellStyle(s.bold());

        return rowIdx;
    }

    private void numCell(Row row, int col, int value, Styles s) {
        Cell c = row.createCell(col);
        c.setCellValue(value);
        c.setCellStyle(s.num());
    }

    private int buildLivestockSection(Sheet sheet, ReportDto report, int startRow, Styles s) {
        List<LivestockRecordDto> records = report.livestock() != null ? report.livestock() : List.of();

        Row titleRow = sheet.createRow(startRow);
        Cell tc = titleRow.createCell(0);
        tc.setCellValue("Livestock Returns – " + MONTH_NAMES[report.month()] + " " + report.year());
        tc.setCellStyle(s.title());
        sheet.addMergedRegion(new CellRangeAddress(startRow, startRow, 0, 2));

        Row headerRow = sheet.createRow(startRow + 1);
        createHdrCell(headerRow, 0, "Category", s.header());
        createHdrCell(headerRow, 1, "Type", s.header());
        createHdrCell(headerRow, 2, "Count", s.header());

        Map<String, List<LivestockRecordDto>> byCategory = records.stream()
            .collect(Collectors.groupingBy(LivestockRecordDto::category, LinkedHashMap::new, Collectors.toList()));

        int rowIdx = startRow + 2;
        int grandTotal = 0;
        for (Map.Entry<String, List<LivestockRecordDto>> e : byCategory.entrySet()) {
            int catTotal = 0;
            for (LivestockRecordDto lr : e.getValue()) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(toTitle(e.getKey()));
                row.createCell(1).setCellValue(lr.type());
                Cell cc = row.createCell(2);
                cc.setCellValue(lr.count());
                cc.setCellStyle(s.num());
                catTotal += lr.count();
            }
            Row subRow = sheet.createRow(rowIdx++);
            Cell sc = subRow.createCell(1);
            sc.setCellValue(toTitle(e.getKey()) + " Total");
            sc.setCellStyle(s.bold());
            Cell sv = subRow.createCell(2);
            sv.setCellValue(catTotal);
            sv.setCellStyle(s.bold());
            grandTotal += catTotal;
        }

        Row grandRow = sheet.createRow(rowIdx++);
        Cell gc = grandRow.createCell(1);
        gc.setCellValue("GRAND TOTAL");
        gc.setCellStyle(s.bold());
        Cell gv = grandRow.createCell(2);
        gv.setCellValue(grandTotal);
        gv.setCellStyle(s.bold());

        return rowIdx;
    }

    private int buildMilkSection(Sheet sheet, ReportDto report, int startRow, Styles s) {
        List<MilkRecordDto> records = report.milk() != null
            ? report.milk().stream().sorted(Comparator.comparingInt(MilkRecordDto::dayOfMonth)).toList()
            : List.of();

        Row titleRow = sheet.createRow(startRow);
        Cell tc = titleRow.createCell(0);
        tc.setCellValue("Milk Production – " + MONTH_NAMES[report.month()] + " " + report.year());
        tc.setCellStyle(s.title());
        sheet.addMergedRegion(new CellRangeAddress(startRow, startRow, 0, 3));

        Row headerRow = sheet.createRow(startRow + 1);
        createHdrCell(headerRow, 0, "Day", s.header());
        createHdrCell(headerRow, 1, "Date", s.header());
        createHdrCell(headerRow, 2, "Litres", s.header());
        createHdrCell(headerRow, 3, "Running Total", s.header());

        double running = 0;
        int rowIdx = startRow + 2;
        for (MilkRecordDto m : records) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(m.dayOfMonth());
            LocalDate date = LocalDate.of(report.year(), report.month(), m.dayOfMonth());
            row.createCell(1).setCellValue(date.format(DATE_FMT));
            double litres = m.litres() != null ? m.litres().doubleValue() : 0;
            Cell lc = row.createCell(2);
            lc.setCellValue(litres);
            lc.setCellStyle(s.num());
            running += litres;
            Cell rc = row.createCell(3);
            rc.setCellValue(running);
            rc.setCellStyle(s.num());
        }

        rowIdx++;
        Row totalRow = sheet.createRow(rowIdx++);
        Cell tl = totalRow.createCell(2);
        tl.setCellValue("Total Litres");
        tl.setCellStyle(s.bold());
        Cell tv = totalRow.createCell(3);
        tv.setCellValue(running);
        tv.setCellStyle(s.bold());

        Row valueRow = sheet.createRow(rowIdx++);
        Cell vl = valueRow.createCell(2);
        vl.setCellValue("Value (×40)");
        vl.setCellStyle(s.bold());
        Cell vv = valueRow.createCell(3);
        vv.setCellValue(running * 40);
        vv.setCellStyle(s.bold());

        return rowIdx;
    }

    private int buildExpensesSection(Sheet sheet, ReportDto report, int startRow, Styles s) {
        List<ExpenseRecordDto> records = report.expenses() != null
            ? report.expenses().stream().sorted(Comparator.comparingInt(ExpenseRecordDto::entryNo)).toList()
            : List.of();

        Row titleRow = sheet.createRow(startRow);
        Cell tc = titleRow.createCell(0);
        tc.setCellValue("Expenses – " + MONTH_NAMES[report.month()] + " " + report.year());
        tc.setCellStyle(s.title());
        sheet.addMergedRegion(new CellRangeAddress(startRow, startRow, 0, 4));

        Row headerRow = sheet.createRow(startRow + 1);
        createHdrCell(headerRow, 0, "No.", s.header());
        createHdrCell(headerRow, 1, "Date", s.header());
        createHdrCell(headerRow, 2, "Supplier / Contractor", s.header());
        createHdrCell(headerRow, 3, "Ref No", s.header());
        createHdrCell(headerRow, 4, "Cost", s.header());

        double total = 0;
        int rowIdx = startRow + 2;
        for (ExpenseRecordDto e : records) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(e.entryNo());
            row.createCell(1).setCellValue(e.date() != null ? e.date().format(DATE_FMT) : "");
            row.createCell(2).setCellValue(e.supplierContractor() != null ? e.supplierContractor() : "");
            row.createCell(3).setCellValue(e.receiptNo() != null ? e.receiptNo() : "");
            double cost = e.cost() != null ? e.cost().doubleValue() : 0;
            Cell cc = row.createCell(4);
            cc.setCellValue(cost);
            cc.setCellStyle(s.num());
            total += cost;
        }

        Row totalRow = sheet.createRow(rowIdx++);
        Cell tl2 = totalRow.createCell(3);
        tl2.setCellValue("TOTAL");
        tl2.setCellStyle(s.bold());
        Cell tv2 = totalRow.createCell(4);
        tv2.setCellValue(total);
        tv2.setCellStyle(s.bold());

        return rowIdx;
    }

    // ── Standalone casual monthly export ──────────────────────────────────────

    public byte[] generateCasualMonthlyExcel(int year, int month,
                                              List<CasualLabourerRow> summary,
                                              List<CasualWorkLogRow> workLog,
                                              List<CasualPaymentRow> payments) {
        try (XSSFWorkbook wb = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Styles s = createStyles(wb);

            // Sheet 1: Summary
            Sheet sumSheet = wb.createSheet("Summary");
            Row sumTitle = sumSheet.createRow(0);
            Cell stc = sumTitle.createCell(0);
            stc.setCellValue("Casual Labour Summary – " + MONTH_NAMES[month] + " " + year);
            stc.setCellStyle(s.title());
            sumSheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 5));

            Row sumHdr = sumSheet.createRow(1);
            createHdrCell(sumHdr, 0, "Name",              s.header());
            createHdrCell(sumHdr, 1, "Phone",             s.header());
            createHdrCell(sumHdr, 2, "Month Earnings",    s.header());
            createHdrCell(sumHdr, 3, "Total Paid",        s.header());
            createHdrCell(sumHdr, 4, "Outstanding",       s.header());

            int ri = 2;
            BigDecimal totEarned = BigDecimal.ZERO, totPaid = BigDecimal.ZERO, totOut = BigDecimal.ZERO;
            for (CasualLabourerRow r : summary) {
                Row row = sumSheet.createRow(ri++);
                row.createCell(0).setCellValue(r.name());
                row.createCell(1).setCellValue(r.phone() != null ? r.phone() : "");
                numDecCell(row, 2, r.monthEarnings(), s);
                numDecCell(row, 3, r.totalPaid(), s);
                numDecCell(row, 4, r.outstanding(), s);
                totEarned = totEarned.add(r.monthEarnings());
                totPaid   = totPaid.add(r.totalPaid());
                totOut    = totOut.add(r.outstanding());
            }
            Row totRow = sumSheet.createRow(ri);
            Cell tc = totRow.createCell(0);
            tc.setCellValue("TOTAL");
            tc.setCellStyle(s.bold());
            numDecCell(totRow, 2, totEarned, s);
            numDecCell(totRow, 3, totPaid,   s);
            numDecCell(totRow, 4, totOut,    s);

            sumSheet.setColumnWidth(0, 6000);
            sumSheet.setColumnWidth(1, 4000);
            for (int c = 2; c <= 4; c++) sumSheet.setColumnWidth(c, 4000);

            // Sheet 2: Work Log
            Sheet wlSheet = wb.createSheet("Work Log");
            Row wlTitle = wlSheet.createRow(0);
            Cell wltc = wlTitle.createCell(0);
            wltc.setCellValue("Daily Work Log – " + MONTH_NAMES[month] + " " + year);
            wltc.setCellStyle(s.title());
            wlSheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 4));

            Row wlHdr = wlSheet.createRow(1);
            createHdrCell(wlHdr, 0, "Labourer",    s.header());
            createHdrCell(wlHdr, 1, "Day",         s.header());
            createHdrCell(wlHdr, 2, "Date",        s.header());
            createHdrCell(wlHdr, 3, "Task",        s.header());
            createHdrCell(wlHdr, 4, "Rate",        s.header());

            int wi = 2;
            for (CasualWorkLogRow r : workLog) {
                Row row = wlSheet.createRow(wi++);
                row.createCell(0).setCellValue(r.labourerName());
                row.createCell(1).setCellValue(r.day());
                java.time.LocalDate date = java.time.LocalDate.of(r.year(), r.month(), r.day());
                row.createCell(2).setCellValue(date.format(DATE_FMT));
                row.createCell(3).setCellValue(r.taskDescription() != null ? r.taskDescription() : "");
                numDecCell(row, 4, r.rate(), s);
            }
            wlSheet.setColumnWidth(0, 6000);
            wlSheet.setColumnWidth(1, 1800);
            wlSheet.setColumnWidth(2, 3500);
            wlSheet.setColumnWidth(3, 7000);
            wlSheet.setColumnWidth(4, 3500);

            // Sheet 3: Payments
            Sheet pySheet = wb.createSheet("Payments");
            Row pyTitle = pySheet.createRow(0);
            Cell pytc = pyTitle.createCell(0);
            pytc.setCellValue("Payment History");
            pytc.setCellStyle(s.title());
            pySheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 4));

            Row pyHdr = pySheet.createRow(1);
            createHdrCell(pyHdr, 0, "Labourer",  s.header());
            createHdrCell(pyHdr, 1, "Date",      s.header());
            createHdrCell(pyHdr, 2, "Amount",    s.header());
            createHdrCell(pyHdr, 3, "Note",      s.header());
            createHdrCell(pyHdr, 4, "Paid By",   s.header());

            int pi = 2;
            BigDecimal grandPayTotal = BigDecimal.ZERO;
            for (CasualPaymentRow r : payments) {
                Row row = pySheet.createRow(pi++);
                row.createCell(0).setCellValue(r.labourerName());
                row.createCell(1).setCellValue(r.date() != null ? r.date().format(DATE_FMT) : "");
                numDecCell(row, 2, r.amount(), s);
                row.createCell(3).setCellValue(r.note() != null ? r.note() : "");
                row.createCell(4).setCellValue(r.paidBy() != null ? r.paidBy() : "");
                grandPayTotal = grandPayTotal.add(r.amount());
            }
            Row pyTotRow = pySheet.createRow(pi);
            Cell ptc = pyTotRow.createCell(1);
            ptc.setCellValue("TOTAL");
            ptc.setCellStyle(s.bold());
            numDecCell(pyTotRow, 2, grandPayTotal, s);

            pySheet.setColumnWidth(0, 6000);
            pySheet.setColumnWidth(1, 3500);
            pySheet.setColumnWidth(2, 3500);
            pySheet.setColumnWidth(3, 8000);
            pySheet.setColumnWidth(4, 5000);

            wb.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate casual labour Excel report", e);
        }
    }

    private void numDecCell(Row row, int col, BigDecimal value, Styles s) {
        Cell c = row.createCell(col);
        c.setCellValue(value != null ? value.doubleValue() : 0);
        c.setCellStyle(s.num());
    }

    private int buildCasualAttendanceSection(Sheet sheet, ReportDto report, int startRow, Styles s) {
        int days = YearMonth.of(report.year(), report.month()).lengthOfMonth();
        List<CasualAttendanceRecordDto> records = report.casualAttendance() != null
                ? report.casualAttendance() : List.of();

        Row titleRow = sheet.createRow(startRow);
        Cell tc = titleRow.createCell(0);
        tc.setCellValue("Casual Labourers – " + MONTH_NAMES[report.month()] + " " + report.year());
        tc.setCellStyle(s.title());
        sheet.addMergedRegion(new CellRangeAddress(startRow, startRow, 0, days + 7));

        Row headerRow = sheet.createRow(startRow + 1);
        createHdrCell(headerRow, 0, "Labourer", s.header());
        for (int d = 1; d <= days; d++) createHdrCell(headerRow, d, String.valueOf(d), s.header());
        createHdrCell(headerRow, days + 1, "Present", s.header());
        createHdrCell(headerRow, days + 2, "Absent",  s.header());
        createHdrCell(headerRow, days + 3, "Annual",  s.header());
        createHdrCell(headerRow, days + 4, "Sick",    s.header());
        createHdrCell(headerRow, days + 5, "Parent",  s.header());
        createHdrCell(headerRow, days + 6, "Days",    s.header());
        createHdrCell(headerRow, days + 7, "Amount Due", s.header());

        // Group by labourer — preserve insertion order
        Map<Integer, String> labourerNames = new LinkedHashMap<>();
        Map<Integer, Map<Integer, CasualAttendanceRecordDto>> byLabourer = new LinkedHashMap<>();
        for (CasualAttendanceRecordDto r : records) {
            labourerNames.put(r.casualLabourerId(), r.casualLabourerName());
            byLabourer.computeIfAbsent(r.casualLabourerId(), k -> new HashMap<>()).put(r.dayOfMonth(), r);
        }

        int rowIdx = startRow + 2;
        double grandTotal = 0;
        for (Map.Entry<Integer, String> entry : labourerNames.entrySet()) {
            Integer labourerId = entry.getKey();
            Map<Integer, CasualAttendanceRecordDto> dayMap = byLabourer.get(labourerId);

            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(entry.getValue());

            int cntP = 0, cntA = 0, cntAL = 0, cntSL = 0, cntPL = 0;
            double amount = 0;
            for (int d = 1; d <= days; d++) {
                CasualAttendanceRecordDto rec = dayMap != null ? dayMap.get(d) : null;
                String status = rec != null ? rec.status() : "A";
                Cell c = row.createCell(d);
                c.setCellValue(status);
                c.setCellStyle(s.center());
                switch (status) {
                    case "P" -> {
                        cntP++;
                        BigDecimal rate = rec.effectiveRate();
                        if (rate != null) amount += rate.doubleValue();
                    }
                    case "A"  -> cntA++;
                    case "AL" -> cntAL++;
                    case "SL" -> cntSL++;
                    case "PL" -> cntPL++;
                }
            }
            numCell(row, days + 1, cntP,  s);
            numCell(row, days + 2, cntA,  s);
            numCell(row, days + 3, cntAL, s);
            numCell(row, days + 4, cntSL, s);
            numCell(row, days + 5, cntPL, s);
            numCell(row, days + 6, days,  s);
            Cell amtCell = row.createCell(days + 7);
            amtCell.setCellValue(amount);
            amtCell.setCellStyle(s.num());
            grandTotal += amount;
        }

        Row totalRow = sheet.createRow(rowIdx++);
        Cell tc2 = totalRow.createCell(0);
        tc2.setCellValue("TOTAL");
        tc2.setCellStyle(s.bold());
        Cell gt = totalRow.createCell(days + 7);
        gt.setCellValue(grandTotal);
        gt.setCellStyle(s.bold());

        return rowIdx;
    }

    // ── Style helpers ──────────────────────────────────────────────────────────

    private Styles createStyles(XSSFWorkbook wb) {
        return new Styles(createHeaderStyle(wb), createTitleStyle(wb), createNumStyle(wb), createBoldStyle(wb), createCenterStyle(wb));
    }

    private void setAttendanceColumnWidths(Sheet sheet, int days) {
        sheet.setColumnWidth(0, 6000);
        for (int d = 1; d <= days; d++) sheet.setColumnWidth(d, 1200);
        for (int i = 1; i <= 6; i++) sheet.setColumnWidth(days + i, 2000);
    }

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

    private CellStyle createCenterStyle(XSSFWorkbook wb) {
        CellStyle s = wb.createCellStyle();
        s.setAlignment(HorizontalAlignment.CENTER);
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
