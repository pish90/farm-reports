package com.farmreports.api.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "data_backups")
@Getter @Setter @NoArgsConstructor
public class DataBackup {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "backup_type", nullable = false, length = 50)
    private String backupType;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by", length = 255)
    private String createdBy;

    @Column(name = "row_count")
    private Integer rowCount;

    @Column(name = "data_csv", nullable = false, columnDefinition = "TEXT")
    private String dataCsv;
}
