package com.farmreports.api.controller;

import com.farmreports.api.dto.*;
import com.farmreports.api.entity.AuditAction;
import com.farmreports.api.security.ClaimsHelper;
import com.farmreports.api.service.AuditService;
import com.farmreports.api.service.CasualLabourerService;
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
@RequestMapping("/farms")
@RequiredArgsConstructor
public class CasualLabourerController {

    private final CasualLabourerService casualLabourerService;
    private final AuditService auditService;

    // ── Labourers ─────────────────────────────────────────────────────────────

    @GetMapping("/{farmId}/casual-labourers")
    public ApiResponse<List<CasualLabourerDto>> getCasualLabourers(
            @PathVariable Integer farmId, Authentication auth) {
        checkFarmAccess(farmId, auth);
        return ApiResponse.ok(casualLabourerService.getActiveCasualLabourers(farmId));
    }

    @PostMapping("/{farmId}/casual-labourers")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CasualLabourerDto> addCasualLabourer(
            @PathVariable Integer farmId,
            @Valid @RequestBody CasualLabourerRequest request,
            Authentication auth) {
        checkFarmAccess(farmId, auth);
        CasualLabourerDto labourer = casualLabourerService.addCasualLabourer(farmId, request);
        auditService.log(AuditAction.CASUAL_LABOURER_ADDED, auth,
                farmId, ClaimsHelper.getFarmName(auth),
                "CasualLabourer", String.valueOf(labourer.id()),
                "Casual labourer added: " + labourer.name());
        return ApiResponse.ok(labourer);
    }

    @DeleteMapping("/{farmId}/casual-labourers/{labourerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivateCasualLabourer(
            @PathVariable Integer farmId,
            @PathVariable Integer labourerId,
            Authentication auth) {
        checkFarmAccess(farmId, auth);
        casualLabourerService.deactivateCasualLabourer(farmId, labourerId);
        auditService.log(AuditAction.CASUAL_LABOURER_DEACTIVATED, auth,
                farmId, ClaimsHelper.getFarmName(auth),
                "CasualLabourer", String.valueOf(labourerId),
                "Casual labourer deactivated (id=" + labourerId + ")");
    }

    // ── Summary ───────────────────────────────────────────────────────────────

    @GetMapping("/{farmId}/casual-labourers/{labourerId}/summary")
    public ApiResponse<CasualLabourerSummaryDto> getSummary(
            @PathVariable Integer farmId,
            @PathVariable Integer labourerId,
            Authentication auth) {
        checkFarmAccess(farmId, auth);
        return ApiResponse.ok(casualLabourerService.getSummary(farmId, labourerId));
    }

    // ── Payments ──────────────────────────────────────────────────────────────

    @PostMapping("/{farmId}/casual-labourers/{labourerId}/payments")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CasualLabourerPaymentDto> recordPayment(
            @PathVariable Integer farmId,
            @PathVariable Integer labourerId,
            @Valid @RequestBody RecordPaymentRequest request,
            Authentication auth) {
        checkFarmAccess(farmId, auth);
        String paidBy = ClaimsHelper.getUserName(auth);
        CasualLabourerPaymentDto payment = casualLabourerService.recordPayment(farmId, labourerId, request, paidBy);
        auditService.log(AuditAction.CASUAL_PAYMENT_RECORDED, auth,
                farmId, ClaimsHelper.getFarmName(auth),
                "CasualLabourerPayment", String.valueOf(payment.id()),
                "Payment of " + payment.amount() + " recorded for " + payment.labourerName());
        return ApiResponse.ok(payment);
    }

    @DeleteMapping("/{farmId}/casual-labourers/{labourerId}/payments/{paymentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePayment(
            @PathVariable Integer farmId,
            @PathVariable Integer labourerId,
            @PathVariable Integer paymentId,
            Authentication auth) {
        checkFarmAccess(farmId, auth);
        casualLabourerService.deletePayment(farmId, labourerId, paymentId);
        auditService.log(AuditAction.CASUAL_PAYMENT_DELETED, auth,
                farmId, ClaimsHelper.getFarmName(auth),
                "CasualLabourerPayment", String.valueOf(paymentId),
                "Payment deleted (id=" + paymentId + ")");
    }

    // ── Work Sessions ─────────────────────────────────────────────────────────

    @GetMapping("/{farmId}/casual-labourers/work-sessions")
    public ApiResponse<List<CasualWorkSessionDto>> getWorkSessions(
            @PathVariable Integer farmId, Authentication auth) {
        checkFarmAccess(farmId, auth);
        return ApiResponse.ok(casualLabourerService.getWorkSessions(farmId));
    }

    @PostMapping("/{farmId}/casual-labourers/work-sessions")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CasualWorkSessionDto> createWorkSession(
            @PathVariable Integer farmId,
            @Valid @RequestBody CreateWorkSessionRequest request,
            Authentication auth) {
        checkFarmAccess(farmId, auth);
        CasualWorkSessionDto session = casualLabourerService.createWorkSession(farmId, request);
        auditService.log(AuditAction.CASUAL_LABOURER_ADDED, auth,
                farmId, ClaimsHelper.getFarmName(auth),
                "CasualWorkSession", String.valueOf(session.id()),
                "Work session created: " + session.activity() + " on " + session.sessionDate());
        return ApiResponse.ok(session);
    }

    @DeleteMapping("/{farmId}/casual-labourers/work-sessions/{sessionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteWorkSession(
            @PathVariable Integer farmId,
            @PathVariable Integer sessionId,
            Authentication auth) {
        checkFarmAccess(farmId, auth);
        casualLabourerService.deleteWorkSession(farmId, sessionId);
        auditService.log(AuditAction.CASUAL_LABOURER_DEACTIVATED, auth,
                farmId, ClaimsHelper.getFarmName(auth),
                "CasualWorkSession", String.valueOf(sessionId),
                "Work session deleted (id=" + sessionId + ")");
    }

    // ── All-Summaries Report ──────────────────────────────────────────────────

    @GetMapping("/{farmId}/casual-labourers/all-summaries")
    public ApiResponse<List<CasualLabourerReportDto>> getAllSummaries(
            @PathVariable Integer farmId, Authentication auth) {
        checkFarmAccess(farmId, auth);
        return ApiResponse.ok(casualLabourerService.getAllSummaries(farmId));
    }

    // ── Monthly payroll (JSON list) ───────────────────────────────────────────

    @GetMapping("/{farmId}/casual-labourers/payroll")
    public ApiResponse<List<CasualPayrollEntryDto>> getMonthlyPayroll(
            @PathVariable Integer farmId,
            @RequestParam Integer year,
            @RequestParam Integer month,
            Authentication auth) {
        checkFarmAccess(farmId, auth);
        return ApiResponse.ok(casualLabourerService.getMonthlyPayroll(farmId, year, month));
    }

    // ── Export ────────────────────────────────────────────────────────────────

    @GetMapping("/{farmId}/casual-labourers/export")
    public ResponseEntity<byte[]> exportMonthlyReport(
            @PathVariable Integer farmId,
            @RequestParam Integer year,
            @RequestParam Integer month,
            Authentication auth) {
        checkFarmAccess(farmId, auth);
        byte[] excel = casualLabourerService.generateMonthlyExcel(farmId, year, month);
        String filename = "casual-labour-" + year + "-" + String.format("%02d", month) + ".xlsx";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excel);
    }

    private void checkFarmAccess(Integer farmId, Authentication auth) {
        if ("ADMIN".equals(ClaimsHelper.getRole(auth))) return;
        if (!farmId.equals(ClaimsHelper.getFarmId(auth))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
    }
}
