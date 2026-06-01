package com.farmreports.api.controller;

import com.farmreports.api.dto.*;
import com.farmreports.api.dto.CasualAttendanceEntryRequest;
import com.farmreports.api.dto.NoteRequest;
import com.farmreports.api.entity.AuditAction;
import com.farmreports.api.security.ClaimsHelper;
import com.farmreports.api.service.AuditService;
import com.farmreports.api.service.ExportService;
import com.farmreports.api.service.ReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;
    private final ExportService exportService;
    private final AuditService auditService;

    @GetMapping
    public ApiResponse<ReportDto> getReport(
            @RequestParam Integer farmId,
            @RequestParam Integer year,
            @RequestParam Integer month,
            Authentication auth) {
        checkFarmAccess(farmId, auth);
        return ApiResponse.ok(reportService.getReport(farmId, year, month));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ReportDto> createReport(
            @Valid @RequestBody CreateReportRequest request, Authentication auth) {
        checkFarmAccess(request.farmId(), auth);
        ReportDto report = reportService.createOrGetReport(
                request.farmId(), request.year(), request.month(),
                ClaimsHelper.getUserId(auth));
        auditService.log(AuditAction.REPORT_CREATED, auth,
                ClaimsHelper.getFarmId(auth), ClaimsHelper.getFarmName(auth),
                "MonthlyReport", String.valueOf(report.id()),
                "Report created for " + request.year() + "-" + String.format("%02d", request.month()));
        return ApiResponse.ok(report);
    }

    @PutMapping("/{id}/attendance")
    public ApiResponse<Void> upsertAttendance(
            @PathVariable Integer id,
            @Valid @RequestBody List<@Valid AttendanceEntryRequest> entries,
            Authentication auth) {
        reportService.upsertAttendance(id, ClaimsHelper.getFarmId(auth), entries);
        auditService.log(AuditAction.ATTENDANCE_UPDATED, auth,
                ClaimsHelper.getFarmId(auth), ClaimsHelper.getFarmName(auth),
                "MonthlyReport", String.valueOf(id),
                "Attendance updated (" + entries.size() + " entries)");
        return ApiResponse.ok();
    }

    @PutMapping("/{id}/livestock")
    public ApiResponse<Void> upsertLivestock(
            @PathVariable Integer id,
            @Valid @RequestBody List<@Valid LivestockEntryRequest> entries,
            Authentication auth) {
        reportService.upsertLivestock(id, ClaimsHelper.getFarmId(auth), entries);
        auditService.log(AuditAction.LIVESTOCK_UPDATED, auth,
                ClaimsHelper.getFarmId(auth), ClaimsHelper.getFarmName(auth),
                "MonthlyReport", String.valueOf(id),
                "Livestock returns updated (" + entries.size() + " entries)");
        return ApiResponse.ok();
    }

    @PutMapping("/{id}/milk")
    public ApiResponse<Void> upsertMilk(
            @PathVariable Integer id,
            @Valid @RequestBody List<@Valid MilkEntryRequest> entries,
            Authentication auth) {
        reportService.upsertMilk(id, ClaimsHelper.getFarmId(auth), entries);
        auditService.log(AuditAction.MILK_UPDATED, auth,
                ClaimsHelper.getFarmId(auth), ClaimsHelper.getFarmName(auth),
                "MonthlyReport", String.valueOf(id),
                "Milk production updated (" + entries.size() + " entries)");
        return ApiResponse.ok();
    }

    @PutMapping("/{id}/expenses")
    public ApiResponse<Void> upsertExpenses(
            @PathVariable Integer id,
            @Valid @RequestBody List<@Valid ExpenseEntryRequest> entries,
            Authentication auth) {
        reportService.upsertExpenses(id, ClaimsHelper.getFarmId(auth), entries);
        auditService.log(AuditAction.EXPENSES_UPDATED, auth,
                ClaimsHelper.getFarmId(auth), ClaimsHelper.getFarmName(auth),
                "MonthlyReport", String.valueOf(id),
                "Expenses updated (" + entries.size() + " entries)");
        return ApiResponse.ok();
    }

    @PutMapping("/{id}/casual-attendance")
    public ApiResponse<Void> upsertCasualAttendance(
            @PathVariable Integer id,
            @Valid @RequestBody List<@Valid CasualAttendanceEntryRequest> entries,
            Authentication auth) {
        reportService.upsertCasualAttendance(id, ClaimsHelper.getFarmId(auth), entries);
        auditService.log(AuditAction.CASUAL_ATTENDANCE_UPDATED, auth,
                ClaimsHelper.getFarmId(auth), ClaimsHelper.getFarmName(auth),
                "MonthlyReport", String.valueOf(id),
                "Casual attendance updated (" + entries.size() + " entries)");
        return ApiResponse.ok();
    }

    @PutMapping("/{id}/attendance-notes")
    public ApiResponse<Void> upsertAttendanceNotes(
            @PathVariable Integer id,
            @Valid @RequestBody NoteRequest request,
            Authentication auth) {
        reportService.upsertAttendanceNotes(id, ClaimsHelper.getFarmId(auth), request);
        auditService.log(AuditAction.ATTENDANCE_NOTES_UPDATED, auth,
                ClaimsHelper.getFarmId(auth), ClaimsHelper.getFarmName(auth),
                "MonthlyReport", String.valueOf(id), "Attendance notes updated");
        return ApiResponse.ok();
    }

    @PutMapping("/{id}/livestock-notes")
    public ApiResponse<Void> upsertLivestockNotes(
            @PathVariable Integer id,
            @Valid @RequestBody NoteRequest request,
            Authentication auth) {
        reportService.upsertLivestockNotes(id, ClaimsHelper.getFarmId(auth), request);
        auditService.log(AuditAction.LIVESTOCK_NOTES_UPDATED, auth,
                ClaimsHelper.getFarmId(auth), ClaimsHelper.getFarmName(auth),
                "MonthlyReport", String.valueOf(id), "Livestock notes updated");
        return ApiResponse.ok();
    }

    @PostMapping("/{id}/submit")
    public ApiResponse<ReportDto> submitReport(@PathVariable Integer id, Authentication auth) {
        ReportDto report = reportService.submitReport(id, ClaimsHelper.getFarmId(auth));
        auditService.log(AuditAction.REPORT_SUBMITTED, auth,
                ClaimsHelper.getFarmId(auth), ClaimsHelper.getFarmName(auth),
                "MonthlyReport", String.valueOf(id), "Report submitted");
        return ApiResponse.ok(report);
    }

    @PostMapping("/{id}/reopen")
    public ApiResponse<ReportDto> reopenReport(@PathVariable Integer id, Authentication auth) {
        if (!"ADMIN".equals(ClaimsHelper.getRole(auth))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can reopen reports");
        }
        ReportDto report = reportService.adminReopenReport(id);
        auditService.log(AuditAction.REPORT_REOPENED, auth,
                null, null, "MonthlyReport", String.valueOf(id), "Report reopened");
        return ApiResponse.ok(report);
    }

    @GetMapping("/{id}")
    public ApiResponse<ReportDto> getReportById(@PathVariable Integer id, Authentication auth) {
        return ApiResponse.ok(reportService.getReportById(
            id, ClaimsHelper.getFarmId(auth), ClaimsHelper.getRole(auth)));
    }

    @GetMapping("/{id}/export")
    public ResponseEntity<byte[]> exportReport(@PathVariable Integer id, Authentication auth) {
        ReportDto report = reportService.getReportById(
            id, ClaimsHelper.getFarmId(auth), ClaimsHelper.getRole(auth));
        byte[] excel = exportService.generateExcel(report);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"farm-report-" + report.year() + "-" +
                String.format("%02d", report.month()) + ".xlsx\"")
            .contentType(MediaType.parseMediaType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
            .body(excel);
    }

    private void checkFarmAccess(Integer farmId, Authentication auth) {
        if (!farmId.equals(ClaimsHelper.getFarmId(auth))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
    }
}
