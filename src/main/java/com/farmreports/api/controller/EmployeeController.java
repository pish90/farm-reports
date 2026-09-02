package com.farmreports.api.controller;

import com.farmreports.api.dto.*;
import com.farmreports.api.entity.AuditAction;
import com.farmreports.api.security.ClaimsHelper;
import com.farmreports.api.service.AuditService;
import com.farmreports.api.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/farms/{farmId}/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;
    private final AuditService auditService;

    // ── Registry ──────────────────────────────────────────────────────────────

    @GetMapping
    public ApiResponse<PageDto<EmployeeDto>> getEmployees(
            @PathVariable Integer farmId,
            @RequestParam(required = false) String employmentType,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication auth) {
        checkFarmAccess(farmId, auth);
        return ApiResponse.ok(employeeService.getEmployees(farmId, employmentType, search, status, page, size));
    }

    @GetMapping("/{id}")
    public ApiResponse<EmployeeDto> getEmployee(
            @PathVariable Integer farmId,
            @PathVariable Integer id,
            Authentication auth) {
        checkFarmAccess(farmId, auth);
        return ApiResponse.ok(employeeService.getEmployee(farmId, id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<EmployeeDto> createEmployee(
            @PathVariable Integer farmId,
            @Valid @RequestBody EmployeeRequest request,
            Authentication auth) {
        checkFarmAccess(farmId, auth);
        EmployeeDto employee = employeeService.createEmployee(farmId, request);
        auditService.log(AuditAction.EMPLOYEE_ADDED, auth,
                farmId, ClaimsHelper.getFarmName(auth),
                "Employee", String.valueOf(employee.id()),
                "Employee added: " + employee.fullName() + " (" + employee.lsNumber() + ")");
        return ApiResponse.ok(employee);
    }

    @PutMapping("/{id}")
    public ApiResponse<EmployeeDto> updateEmployee(
            @PathVariable Integer farmId,
            @PathVariable Integer id,
            @Valid @RequestBody EmployeeRequest request,
            Authentication auth) {
        checkFarmAccess(farmId, auth);
        EmployeeDto employee = employeeService.updateEmployee(farmId, id, request);
        auditService.log(AuditAction.EMPLOYEE_UPDATED, auth,
                farmId, ClaimsHelper.getFarmName(auth),
                "Employee", String.valueOf(employee.id()),
                "Employee updated: " + employee.fullName() + " (" + employee.lsNumber() + ")");
        return ApiResponse.ok(employee);
    }

    // ── Payments (salaried) ───────────────────────────────────────────────────

    @GetMapping("/{id}/summary")
    public ApiResponse<EmployeeSummaryDto> getSummary(
            @PathVariable Integer farmId,
            @PathVariable Integer id,
            Authentication auth) {
        checkFarmAccess(farmId, auth);
        return ApiResponse.ok(employeeService.getSummary(farmId, id));
    }

    @GetMapping("/{id}/ledger")
    public ApiResponse<EmployeeLedgerDto> getLedger(
            @PathVariable Integer farmId,
            @PathVariable Integer id,
            @RequestParam Integer year,
            Authentication auth) {
        checkFarmAccess(farmId, auth);
        return ApiResponse.ok(employeeService.getEmployeeLedger(farmId, id, year));
    }

    @PostMapping("/{id}/payments")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<EmployeePaymentDto> recordPayment(
            @PathVariable Integer farmId,
            @PathVariable Integer id,
            @Valid @RequestBody RecordPaymentRequest request,
            Authentication auth) {
        checkFarmAccess(farmId, auth);
        String paidBy = ClaimsHelper.getUserName(auth);
        EmployeePaymentDto payment = employeeService.recordPayment(farmId, id, request, paidBy);
        auditService.log(AuditAction.EMPLOYEE_PAYMENT_RECORDED, auth,
                farmId, ClaimsHelper.getFarmName(auth),
                "EmployeePayment", String.valueOf(payment.id()),
                "Payment of " + payment.amount() + " recorded for " + payment.employeeName());
        return ApiResponse.ok(payment);
    }

    @DeleteMapping("/{id}/payments/{paymentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePayment(
            @PathVariable Integer farmId,
            @PathVariable Integer id,
            @PathVariable Integer paymentId,
            Authentication auth) {
        checkFarmAccess(farmId, auth);
        employeeService.deletePayment(farmId, id, paymentId);
        auditService.log(AuditAction.EMPLOYEE_PAYMENT_DELETED, auth,
                farmId, ClaimsHelper.getFarmName(auth),
                "EmployeePayment", String.valueOf(paymentId),
                "Employee payment deleted (id=" + paymentId + ")");
    }

    private void checkFarmAccess(Integer farmId, Authentication auth) {
        if ("ADMIN".equals(ClaimsHelper.getRole(auth))) return;
        if (!farmId.equals(ClaimsHelper.getFarmId(auth))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
    }
}
