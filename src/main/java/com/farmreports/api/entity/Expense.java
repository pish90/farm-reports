package com.farmreports.api.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "expenses")
@Getter
@Setter
@NoArgsConstructor
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id", nullable = false)
    private MonthlyReport report;

    @Column(name = "entry_no", nullable = false)
    private Integer entryNo;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "supplier_contractor")
    private String supplierContractor;

    @Column(name = "ref_no", length = 100)
    private String refNo;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal cost;
}
