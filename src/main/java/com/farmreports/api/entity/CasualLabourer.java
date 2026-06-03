package com.farmreports.api.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "casual_labourers")
@Getter
@Setter
@NoArgsConstructor
public class CasualLabourer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farm_id", nullable = false)
    private Farm farm;

    @Column(nullable = false)
    private String name;

    @Column
    private String phone;

    @Column(name = "photo_data")
    private byte[] photoData;

    @Column(name = "photo_mime_type")
    private String photoMimeType;

    @Column(name = "default_daily_rate")
    private BigDecimal defaultDailyRate;

    @Column(nullable = false)
    private boolean active = true;
}
