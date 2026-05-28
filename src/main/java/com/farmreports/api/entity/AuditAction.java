package com.farmreports.api.entity;

public enum AuditAction {
    // Authentication
    LOGIN,
    LOGIN_FAILED,
    PASSWORD_CHANGED,
    PASSWORD_RESET,

    // Report lifecycle
    REPORT_CREATED,
    REPORT_SUBMITTED,
    REPORT_REOPENED,

    // Report data entry
    ATTENDANCE_UPDATED,
    LIVESTOCK_UPDATED,
    MILK_UPDATED,
    EXPENSES_UPDATED,
    ATTENDANCE_NOTES_UPDATED,
    LIVESTOCK_NOTES_UPDATED,

    // Workers
    WORKER_ADDED,
    WORKER_DEACTIVATED,

    // Admin
    EXCEL_EXPORTED
}
