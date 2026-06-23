package com.farmreports.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BackupScheduler {

    private final AdminService adminService;

    // Runs at 02:00 on the 1st of every month
    @Scheduled(cron = "0 0 2 1 * *")
    public void monthlyAttendanceBackup() {
        try {
            var backup = adminService.createBackup("MONTHLY", "scheduler");
            log.info("Monthly attendance backup created: id={} rows={}", backup.getId(), backup.getRowCount());
        } catch (Exception e) {
            log.error("Monthly attendance backup failed", e);
        }
    }
}
