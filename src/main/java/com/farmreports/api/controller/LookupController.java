package com.farmreports.api.controller;

import com.farmreports.api.dto.ApiResponse;
import com.farmreports.api.dto.BusinessUnitDto;
import com.farmreports.api.dto.ExpenseCategoryDto;
import com.farmreports.api.repository.BusinessUnitRepository;
import com.farmreports.api.repository.ExpenseCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/lookup")
@RequiredArgsConstructor
public class LookupController {

    private final ExpenseCategoryRepository categoryRepo;
    private final BusinessUnitRepository businessUnitRepo;

    @GetMapping("/expense-categories")
    public ApiResponse<List<ExpenseCategoryDto>> getCategories() {
        List<ExpenseCategoryDto> dtos = categoryRepo.findAll().stream()
                .map(c -> new ExpenseCategoryDto(c.getId(), c.getAccountCode(), c.getAccountName()))
                .toList();
        return ApiResponse.ok(dtos);
    }

    @GetMapping("/business-units")
    public ApiResponse<List<BusinessUnitDto>> getBusinessUnits() {
        List<BusinessUnitDto> dtos = businessUnitRepo.findAll().stream()
                .map(b -> new BusinessUnitDto(b.getId(), b.getCode(), b.getName()))
                .toList();
        return ApiResponse.ok(dtos);
    }
}
