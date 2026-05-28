package com.farmreports.api.controller;

import com.farmreports.api.dto.ApiResponse;
import com.farmreports.api.dto.LivestockTypeDto;
import com.farmreports.api.dto.WorkerDto;
import com.farmreports.api.dto.WorkerRequest;
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
                "Worker", String.valueOf(worker.id()),
                "Worker added: " + worker.name());
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
                "Worker", String.valueOf(workerId),
                "Worker deactivated (id=" + workerId + ")");
    }

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
