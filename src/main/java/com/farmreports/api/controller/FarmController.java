package com.farmreports.api.controller;

import com.farmreports.api.dto.ApiResponse;
import com.farmreports.api.dto.LivestockTypeDto;
import com.farmreports.api.dto.WorkerDto;
import com.farmreports.api.security.ClaimsHelper;
import com.farmreports.api.service.FarmService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/farms")
@RequiredArgsConstructor
public class FarmController {

    private final FarmService farmService;

    @GetMapping("/{farmId}/workers")
    public ApiResponse<List<WorkerDto>> getWorkers(@PathVariable Integer farmId, Authentication auth) {
        checkFarmAccess(farmId, auth);
        return ApiResponse.ok(farmService.getActiveWorkers(farmId));
    }

    @GetMapping("/{farmId}/livestock-types")
    public ApiResponse<Map<String, List<LivestockTypeDto>>> getLivestockTypes(
            @PathVariable Integer farmId, Authentication auth) {
        checkFarmAccess(farmId, auth);
        return ApiResponse.ok(farmService.getLivestockTypesByCategory(farmId));
    }

    private void checkFarmAccess(Integer farmId, Authentication auth) {
        if (!farmId.equals(ClaimsHelper.getFarmId(auth))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
    }
}
