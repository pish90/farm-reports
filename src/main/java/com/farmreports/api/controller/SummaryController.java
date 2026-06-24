package com.farmreports.api.controller;

import com.farmreports.api.dto.ApiResponse;
import com.farmreports.api.dto.LivestockSummaryDto;
import com.farmreports.api.dto.MilkSummaryDto;
import com.farmreports.api.security.ClaimsHelper;
import com.farmreports.api.service.SummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/reports/summary")
@RequiredArgsConstructor
public class SummaryController {

    private final SummaryService summaryService;

    @GetMapping("/milk")
    public ApiResponse<List<MilkSummaryDto>> getMilkSummary(
            @RequestParam Integer farmId,
            @RequestParam Integer year,
            Authentication auth) {
        checkFarmAccess(farmId, auth);
        return ApiResponse.ok(summaryService.getMilkSummary(farmId, year));
    }

    @GetMapping("/livestock")
    public ApiResponse<List<LivestockSummaryDto>> getLivestockSummary(
            @RequestParam Integer farmId,
            @RequestParam Integer year,
            Authentication auth) {
        checkFarmAccess(farmId, auth);
        return ApiResponse.ok(summaryService.getLivestockSummary(farmId, year));
    }

    private void checkFarmAccess(Integer farmId, Authentication auth) {
        if (!"ADMIN".equals(ClaimsHelper.getRole(auth)) &&
                !farmId.equals(ClaimsHelper.getFarmId(auth))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
    }
}
