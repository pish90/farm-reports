package com.farmreports.api.dto;

public record EmployeeAnnualPayrollDto(
        Integer employeeId,
        String employeeName,
        String lsNumber,
        String status,
        EmployeeLedgerDto ledger
) {}
