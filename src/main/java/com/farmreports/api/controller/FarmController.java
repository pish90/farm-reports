package com.farmreports.api.controller;

import com.farmreports.api.dto.*;
import com.farmreports.api.entity.AuditAction;
import com.farmreports.api.security.ClaimsHelper;
import com.farmreports.api.service.AuditService;
import com.farmreports.api.service.FarmService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/farms")
@RequiredArgsConstructor
public class FarmController {

    private final FarmService farmService;
    private final AuditService auditService;

    // ── Workers ──────────────────────────────────────────────────────────────

    @GetMapping("/{farmId}/workers")
    public ApiResponse<List<WorkerDto>> getWorkers(@PathVariable Integer farmId, Authentication auth) {
        checkFarmAccess(farmId, auth);
        return ApiResponse.ok(farmService.getActiveWorkers(farmId));
    }

    @PostMapping("/{farmId}/workers")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<WorkerDto> addWorker(
            @PathVariable Integer farmId,
            @Valid @RequestBody WorkerRequest request,
            Authentication auth) {
        checkFarmAccess(farmId, auth);
        WorkerDto worker = farmService.addWorker(farmId, request);
        auditService.log(AuditAction.WORKER_ADDED, auth,
                farmId, ClaimsHelper.getFarmName(auth),
                "Employee", String.valueOf(worker.id()),
                "Worker added: " + worker.name() + " (" + worker.employeeId() + ")");
        return ApiResponse.ok(worker);
    }

    @PutMapping("/{farmId}/workers/{workerId}")
    public ApiResponse<WorkerDto> updateWorker(
            @PathVariable Integer farmId,
            @PathVariable Integer workerId,
            @Valid @RequestBody WorkerRequest request,
            Authentication auth) {
        checkFarmAccess(farmId, auth);
        WorkerDto worker = farmService.updateWorker(farmId, workerId, request);
        auditService.log(AuditAction.WORKER_UPDATED, auth,
                farmId, ClaimsHelper.getFarmName(auth),
                "Employee", String.valueOf(worker.id()),
                "Worker updated: " + worker.name() + " (" + worker.employeeId() + ")");
        return ApiResponse.ok(worker);
    }

    @DeleteMapping("/{farmId}/workers/{workerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivateWorker(
            @PathVariable Integer farmId,
            @PathVariable Integer workerId,
            Authentication auth) {
        checkFarmAccess(farmId, auth);
        farmService.deactivateWorker(farmId, workerId);
        auditService.log(AuditAction.WORKER_DEACTIVATED, auth,
                farmId, ClaimsHelper.getFarmName(auth),
                "Employee", String.valueOf(workerId),
                "Worker deactivated (id=" + workerId + ")");
    }

    // ── Departments ──────────────────────────────────────────────────────────

    @GetMapping("/{farmId}/departments")
    public ApiResponse<List<DepartmentDto>> getDepartments(@PathVariable Integer farmId, Authentication auth) {
        checkFarmAccess(farmId, auth);
        return ApiResponse.ok(farmService.getDepartments(farmId));
    }

    @PostMapping("/{farmId}/departments")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<DepartmentDto> addDepartment(
            @PathVariable Integer farmId,
            @Valid @RequestBody DepartmentRequest request,
            Authentication auth) {
        checkFarmAccess(farmId, auth);
        DepartmentDto dept = farmService.addDepartment(farmId, request);
        auditService.log(AuditAction.DEPARTMENT_ADDED, auth,
                farmId, ClaimsHelper.getFarmName(auth),
                "Department", String.valueOf(dept.id()),
                "Department added: " + dept.name());
        return ApiResponse.ok(dept);
    }

    @DeleteMapping("/{farmId}/departments/{deptId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDepartment(
            @PathVariable Integer farmId,
            @PathVariable Integer deptId,
            Authentication auth) {
        checkFarmAccess(farmId, auth);
        farmService.deleteDepartment(farmId, deptId);
        auditService.log(AuditAction.DEPARTMENT_DELETED, auth,
                farmId, ClaimsHelper.getFarmName(auth),
                "Department", String.valueOf(deptId),
                "Department deleted (id=" + deptId + ")");
    }

    // ── Livestock types ──────────────────────────────────────────────────────

    @GetMapping("/{farmId}/livestock-types")
    public ApiResponse<Map<String, List<LivestockTypeDto>>> getLivestockTypes(
            @PathVariable Integer farmId, Authentication auth) {
        checkFarmAccess(farmId, auth);
        return ApiResponse.ok(farmService.getLivestockTypesByCategory(farmId));
    }

    private void checkFarmAccess(Integer farmId, Authentication auth) {
        if ("ADMIN".equals(ClaimsHelper.getRole(auth))) return;
        if (!farmId.equals(ClaimsHelper.getFarmId(auth))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
    }
}
