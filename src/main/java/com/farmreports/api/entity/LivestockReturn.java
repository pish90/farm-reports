package com.farmreports.api.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "livestock_returns")
@Getter
@Setter
@NoArgsConstructor
public class LivestockReturn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id", nullable = false)
    private MonthlyReport report;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "livestock_type_id", nullable = false)
    private LivestockType livestockType;

    @Column(nullable = false)
    private Integer count;
}
