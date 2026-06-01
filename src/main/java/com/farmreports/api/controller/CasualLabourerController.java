package com.farmreports.api.controller;

import com.farmreports.api.dto.ApiResponse;
import com.farmreports.api.dto.CasualLabourerDto;
import com.farmreports.api.dto.CasualLabourerRequest;
import com.farmreports.api.entity.AuditAction;
import com.farmreports.api.security.ClaimsHelper;
import com.farmreports.api.service.AuditService;
import com.farmreports.api.service.CasualLabourerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

    private void checkFarmAccess(Integer farmId, Authentication auth) {
        if ("ADMIN".equals(ClaimsHelper.getRole(auth))) return;
        if (!farmId.equals(ClaimsHelper.getFarmId(auth))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
    }
}
