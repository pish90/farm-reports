package com.farmreports.api.repository;

import com.farmreports.api.entity.DataBackup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DataBackupRepository extends JpaRepository<DataBackup, Integer> {
}
