package com.farmreports.api.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "casual_attendance")
@Getter
@Setter
@NoArgsConstructor
public class CasualAttendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id", nullable = false)
    private MonthlyReport report;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "casual_labourer_id", nullable = false)
    private CasualLabourer casualLabourer;

    @Column(name = "day_of_month", nullable = false)
    private Integer dayOfMonth;

    @Column(nullable = false)
    private boolean present;

    @Column(length = 3, nullable = false)
    private String status = "A";

    @Column(name = "rate_override")
    private BigDecimal rateOverride;

    @Column(name = "task_description", length = 255)
    private String taskDescription;
}
