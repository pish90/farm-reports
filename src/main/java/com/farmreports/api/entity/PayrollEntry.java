package com.farmreports.api.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payroll_entries")
@Getter
@Setter
@NoArgsConstructor
public class PayrollEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "farm_id", nullable = false)
    private Integer farmId;

    @Column(nullable = false)
    private Integer year;

    @Column(nullable = false)
    private Integer month;

    @Column(name = "employee_id", nullable = false)
    private Integer employeeId;

    @Column(name = "salary_rate", precision = 12, scale = 2)
    private BigDecimal salaryRate;

    @Column(name = "days_worked")
    private Integer daysWorked;

    @Column(name = "gross_salary", precision = 12, scale = 2)
    private BigDecimal grossSalary;

    @Column(precision = 12, scale = 2)
    private BigDecimal loans;

    @Column(name = "amount_paid", precision = 12, scale = 2)
    private BigDecimal amountPaid;

    @Column(name = "amount_remaining", precision = 12, scale = 2)
    private BigDecimal amountRemaining;

    @Column(length = 500)
    private String notes;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
