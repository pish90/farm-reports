package com.farmreports.api.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "casual_work_entries")
@Getter
@Setter
@NoArgsConstructor
public class CasualWorkEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private CasualWorkSession session;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "casual_labourer_id", nullable = false)
    private Employee employee;

    @Column(name = "rate_override")
    private BigDecimal rateOverride;

    public BigDecimal effectiveRate() {
        return rateOverride != null ? rateOverride : session.getDefaultDailyRate();
    }
}
