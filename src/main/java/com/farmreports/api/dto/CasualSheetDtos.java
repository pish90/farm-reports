package com.farmreports.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DTOs for "Scan Casual Sheets": handwritten weekly casual pay sheets photographed on the farm,
 * read by Claude one photo at a time, reviewed/corrected by an admin, then saved as casual work
 * sessions (+ payments for signed rows).
 */
public final class CasualSheetDtos {

    private CasualSheetDtos() {}

    // ---- scan (one photo) ----

    /** Sheet heading as read from the photo. {@code present=false} for continuation pages. */
    public record ScannedHeading(
            boolean present,
            String activity,
            String periodText,
            LocalDate startDate,
            LocalDate endDate,
            BigDecimal ratePerDay) {}

    /** An existing employee the handwritten name may refer to, best first. */
    public record NameMatch(Integer employeeId, String name, String lsNumber, boolean active, double score) {}

    public record ScannedRow(
            Integer lineNo,
            String nameAsWritten,
            String daysWorked,
            BigDecimal deduction,
            BigDecimal netAmount,
            String rawAmount,
            String signMark,
            boolean paid,
            boolean uncertain,
            String uncertainReason,
            /** Set when one candidate is a confident, unambiguous match. */
            Integer matchedEmployeeId,
            List<NameMatch> candidates,
            /** Best guess at first/last name if this person has to be added as a new casual. */
            String suggestedFirstName,
            String suggestedLastName) {}

    public record ScanPageResult(
            String fileName,
            boolean isCasualPaySheet,
            ScannedHeading heading,
            List<ScannedRow> rows,
            BigDecimal writtenTotal,
            String notes) {}

    // ---- check (before saving) ----

    public record CheckSheet(String key, LocalDate date, String activity, List<Integer> employeeIds) {}

    public record CheckRequest(Integer farmId, List<CheckSheet> sheets) {}

    /** Per sheet: who already has spreadsheet-imported pay for that month, and whether an identical sheet was saved before. */
    public record CheckSheetResult(String key, List<Integer> alreadyImportedEmployeeIds, boolean duplicateSheet) {}

    public record CheckResult(List<CheckSheetResult> sheets) {}

    // ---- save ----

    /** {@code employeeId} null means "add as new casual" using newFirstName/newLastName. */
    public record SaveRow(Integer employeeId, String newFirstName, String newLastName,
                          BigDecimal amount, boolean paid) {}

    /**
     * {@code confirmDuplicate}: the reviewer has confirmed this is a different sheet even though one with the
     * same activity and date is already saved (or appears earlier in the same request). Without it, save refuses.
     */
    public record SaveSheet(String activity, String periodText, LocalDate date,
                            BigDecimal ratePerDay, List<SaveRow> rows, boolean confirmDuplicate) {}

    public record SaveRequest(Integer farmId, List<SaveSheet> sheets) {}

    public record SaveResult(int sheetsSaved, int entriesSaved, int paymentsRecorded,
                             int employeesCreated, BigDecimal totalEarned, BigDecimal totalPaid) {}
}
