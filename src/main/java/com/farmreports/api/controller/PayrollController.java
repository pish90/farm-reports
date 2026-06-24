package com.farmreports.api.controller;

import com.farmreports.api.dto.ApiResponse;
import com.farmreports.api.dto.PayrollEntryRequest;
import com.farmreports.api.dto.PayrollRecordDto;
import com.farmreports.api.dto.PayrollSummaryDto;
import com.farmreports.api.entity.AuditAction;
import com.farmreports.api.security.ClaimsHelper;
import com.farmreports.api.service.AuditService;
import com.farmreports.api.service.PayrollService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/reports/payroll")
@RequiredArgsConstructor
public class PayrollController {

    private final PayrollService payrollService;
    private final AuditService auditService;

    @GetMapping
    public ApiResponse<List<PayrollRecordDto>> getPayroll(
            @RequestParam Integer farmId,
            @RequestParam Integer year,
            @RequestParam Integer month,
            Authentication auth) {
        checkFarmAccess(farmId, auth);
        return ApiResponse.ok(payrollService.getPayroll(farmId, year, month));
    }

    @PutMapping
    public ApiResponse<Void> upsertPayroll(
            @RequestParam Integer farmId,
            @RequestParam Integer year,
            @RequestParam Integer month,
            @RequestBody List<PayrollEntryRequest> entries,
            Authentication auth) {
        checkFarmAccess(farmId, auth);
        payrollService.upsertPayroll(farmId, year, month, entries);
        auditService.log(AuditAction.PAYROLL_UPDATED, auth,
                ClaimsHelper.getFarmId(auth), ClaimsHelper.getFarmName(auth),
                "PayrollEntry", farmId + "-" + year + "-" + month,
                "Payroll updated for " + year + "-" + String.format("%02d", month) + " (" + entries.size() + " entries)");
        return ApiResponse.ok();
    }

    @GetMapping("/summary")
    public ApiResponse<List<PayrollSummaryDto>> getAnnualPayrollSummary(
            @RequestParam Integer farmId,
            @RequestParam Integer year,
            Authentication auth) {
        checkFarmAccess(farmId, auth);
        return ApiResponse.ok(payrollService.getAnnualPayrollSummary(farmId, year));
    }

    private void checkFarmAccess(Integer farmId, Authentication auth) {
        if ("ADMIN".equals(ClaimsHelper.getRole(auth))) return;
        if (!farmId.equals(ClaimsHelper.getFarmId(auth))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
    }
}
