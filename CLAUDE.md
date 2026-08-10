# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# Build
mvn clean package

# Run locally
mvn spring-boot:run

# Run all tests
mvn test

# Run a single test class
mvn test -Dtest=AuthControllerTest

# Generate a BCrypt hash for a password (edit HashGen.java first)
# Run src/main/java/com/farmreports/api/util/HashGen.java as a main class
```

## Architecture Overview

Spring Boot 3.2.5 / Java 21 REST API backed by PostgreSQL. Deployed on Railway at `https://farm-reports-production.up.railway.app`. All endpoints are prefixed with `/api` (set via `server.servlet.context-path`).

**Layers:** `controller` → `service` → `repository` (Spring Data JPA) + `entity`. DTOs live in `dto/`, security in `security/`.

**Database:** Schema is owned entirely by Flyway (`src/main/resources/db/migration/`). JPA runs with `ddl-auto: validate` — Hibernate never creates or alters tables. Every schema change must be a new `Vxx__description.sql` file; never modify an existing migration (Flyway validates checksums).

## Key Patterns

### Response envelope
All endpoints return `ApiResponse<T>` — a record with `success`, `data`, and `message` fields. Use `ApiResponse.ok(data)`, `ApiResponse.ok()`, or `ApiResponse.error(msg)`. Errors flow through `GlobalExceptionHandler`, which maps `ResponseStatusException` → `ApiResponse.error()`.

### JWT and authentication
`JwtAuthFilter` verifies the `Authorization: Bearer` header on every request and stores the raw `Claims` object as the `Authentication` principal. Controllers extract identity from it via `ClaimsHelper`:

```java
ClaimsHelper.getUserId(auth)    // Integer
ClaimsHelper.getFarmId(auth)    // Integer — null for ADMIN users
ClaimsHelper.getRole(auth)      // String: "ADMIN", "MANAGER", "WORKER", "OPERATIONS_MANAGER"
ClaimsHelper.getFarmName(auth)  // String
ClaimsHelper.getUserName(auth)  // String
```

The JWT payload encodes: `userId`, `userName`, `role`, `farmId`, `farmName`, `mustChangePassword`.

### Multi-tenancy / farm isolation
Every non-admin user belongs to exactly one farm. Controllers call a `checkFarmAccess(farmId, auth)` guard before operating on farm-owned data. ADMIN role bypasses the farm check.

Role hierarchy for endpoint access:
- `/auth/**` — public
- `/reports/**`, `/farms/**` — any authenticated user, own farm only
- `/admin/**` — ADMIN, MANAGER, or OPERATIONS_MANAGER (specific endpoints further restricted to ADMIN-only)

### Upsert pattern
Mutable report sections (attendance, livestock, milk, expenses, casual attendance) all use delete-then-reinsert within a single transaction. The service deletes existing rows for the report and saves the full new list. Incoming payloads are deduplicated by natural key before saving to guard against mobile clients sending duplicate rows.

### Audit logging
Every mutating controller method calls `auditService.log(AuditAction, auth, farmId, farmName, entityType, entityId, description)` immediately after the service call. `AuditAction` is an enum in `entity/AuditAction.java`. `AuditService` never throws — failures are logged and silently swallowed so they don't break the main request.

### Entities
All JPA entities use Lombok `@Getter @Setter @NoArgsConstructor`. Enum columns (e.g. `ReportStatus`, `UserRole`, `LivestockCategory`) are stored as `STRING`. Relationships use `FetchType.LAZY` throughout.

### JdbcTemplate alongside JPA
`ReportService` injects both `JpaRepositories` and `JdbcTemplate`. Use `JdbcTemplate` for simple DELETE/INSERT where JPA would over-complicate (e.g. notes tables not backed by JPA entities).

## Domain Model

- **Farm** — top-level tenant. Each farm has workers, livestock types, and users.
- **MonthlyReport** — one per farm per (year, month), unique constraint enforced. Status: `DRAFT` → `SUBMITTED`. Sections: attendance, livestock returns, milk production, expenses, casual attendance.
- **Worker** — permanent farm staff (salaried). Attendance tracked per report with status `P/A/WA/H/SL/AL` (P=present, WA=work assignment added later).
- **CasualLabourer** — seasonal worker. Casual work tracked via the work-session model (see below).
- **CasualWorkSession** — a date + activity + default daily rate for a farm. Multiple labourers attach to a session via `CasualWorkEntry` (with optional per-entry `rate_override`). This replaced the old `casual_attendance` table (V24 migration); the old table is kept for historical data only.
- **Expense** — has optional `ExpenseCategory` (account code) and `BusinessUnit`, plus a list of `ExpenseApportionment` rows for cost splitting.
- **PayrollEntry** — one row per (farm, year, month, employee). Flat entity — stores `farmId` and `employeeId` as plain integers (no `@ManyToOne`). Fields: `salaryRate`, `daysWorked`, `grossSalary`, `loans`, `amountPaid`, `amountRemaining`, `notes`, `updatedAt`. UNIQUE on `(farm_id, year, month, employee_id)`.
  - `GET /reports/payroll?farmId&year&month` — returns entries for that period, seeding from previous month if none exist (copies salary_rate/loans, recalculates daysWorked from attendance P/WA statuses, falls back to all ACTIVE SALARIED employees if no prior month data either).
  - `PUT /reports/payroll?farmId&year&month` — upsert (delete-then-reinsert). Audit: `PAYROLL_UPDATED`.
  - `GET /reports/payroll/summary?farmId&year` — queries `summary_payroll` view.
  - Visible in mobile only to MANAGER and ADMIN roles. Admin sees a farm-picker chip bar (loaded via `getFarmLiveStatus`) since admin has no farmId in JWT.

### Annual Summary Views (V32)
- `summary_payroll` — `SUM(gross/loans/paid/remaining)` grouped by `farm_id, year, month` — queried by `GET /reports/payroll/summary`.
- `summary_milk_prod` — `SUM(litres)` grouped by `farm_id, year, month` — queried by `GET /reports/summary/milk?farmId&year`.
- `summary_livestock` — `SUM(count)` grouped by `farm_id, year, month, category, type` — queried by `GET /reports/summary/livestock?farmId&year`.

## Environment Variables

| Variable | Description | Default |
|---|---|---|
| `PGHOST / PGPORT / PGDATABASE` | PostgreSQL connection | `localhost/5432/farm_reports` |
| `PGUSER / PGPASSWORD` (or `DB_USERNAME / DB_PASSWORD`) | DB credentials | — |
| `JWT_SECRET` | HS256 signing key (min 32 chars) | insecure dev default |
| `JWT_EXPIRATION_MS` | Token lifetime | `86400000` (24 h) |
| `CORS_ALLOWED_ORIGINS` | Comma-separated origins | `http://localhost:5173,http://localhost:3000` |
| `ANTHROPIC_API_KEY` | For receipt OCR (optional) | — |

## Database / Flyway Notes

- Seed data is in `V2__seed.sql` (farms, users, livestock types). Workers and reports are created via the API.
- The confirmed valid BCrypt hash for the default password `"changeme"` is `$2a$10$Jo5jGv4K781lpnmvNTMuOelw2K22RenTJD3MFl22XBdAqoFoY7Srm` (round-trip verified 2026-08-06 via `HashGen.java` after the previously-documented hash here turned out not to match `"changeme"` — see V38). Always re-verify a hash with `HashGen.java`'s round-trip check before trusting it in a migration; don't copy hashes between migrations without confirming.
- When adding new livestock categories, add them to the `LivestockCategory` enum AND insert the new `livestock_types` rows via a new migration.

### Migration history (latest first)
| Version | Description |
|---|---|
| V40 | Renames CATTLE `STEER`→`COMPOUND` (client term for animals not taken out to graze); corrects SHEEP sex labels so `RAMS`=male/`EWES`=female for both Les A (was `MALE`/`FEMALE`, never renamed) and Les B (V28 had renamed them backwards). See "Bulk XLSX import" below — these fixes align `livestock_types` with the client's import template. |
| V39 | Splits legacy `employees.first_name` into first/last name on the first space, for rows migrated from the old `workers`/`casual_labourers` tables (V29) that had the full name jammed into `first_name` with no `last_name`. Single-word names and rows that already have a `last_name` are left untouched. |
| V38 | Fixes Silas Khayundi's password hash — V37's hash didn't actually verify against `changeme` (copied from a stale CLAUDE.md note). |
| V37 | Adds ADMIN login for client (Silas Khayundi, `skhayundi@gmail.com`), farm_id NULL, temp password `changeme`, `must_change_password=true`. |
| V36 | Appends a farm letter to existing `ls_number` values (e.g. `LS2001K`). See LS number format below. |
| V35 | Adds `gender VARCHAR(10)` (nullable) to `employees` table. |
| V34 | Employee profile — adds `ls_number` (global unique, starts LS2001), `date_of_birth`, `national_id` to `employees`; backfills LS numbers for all existing employees in ID order; creates `employee_payments` table for salaried staff payments. |
| V33 | Payroll seed data — Jan–May 2026 for all 5 farms (360 rows), extracted from Excel backups. Idempotent via ON CONFLICT DO UPDATE. Names matched via `UPPER(first_name)` on `employees` table. |
| V32 | Annual summary views: `summary_payroll`, `summary_milk_prod`, `summary_livestock` |
| V31 | `payroll_entries` table + `summary_payroll` view |
| V30 | (previous — check file for details) |
| V29 | Employee unification — merged `workers` into `employees` table; full name stored in `first_name` column (single field, no last_name for migrated workers) |

**Next migration must be V41.**

### LS number format
`ls_number` is `LS` + a globally shared sequence + a single farm letter fixed at hire time, e.g. `LS2001K`. The letter is looked up from the farm name at generation time (`EmployeeIdService.letterFor`) and never changes even if the employee later transfers farms. Mapping: `Les A`→A, `Les B`→B, `Kenlet`→K, `Siyoi`→S, `Matunda`→M.

### Master employee registry
`GET /admin/employees` — ADMIN-only endpoint returning every employee across all 5 farms (each `EmployeeDto` now includes `farmId`/`farmName`), ordered by farm then name. Intended for an upcoming admin webpage. Not exposed to MANAGER/OPERATIONS_MANAGER since farm employee data is confidential between farms; those roles remain restricted to `GET /farms/{farmId}/employees` for their own farm.

### Bulk XLSX import (ADMIN-only)
Three importers accept the client's multi-sheet workbook (`employee_import_template`, `livestock_import_template`, `Milk_import_template`), read via Apache POI (`poi-ooxml`, already a dependency alongside `commons-csv`). All follow the same validate-every-row-first, insert-only-if-zero-errors contract as the original CSV import (`ImportResult`/`ImportRowError` DTOs for livestock/milk; `EmployeeCsvImportResult`/`EmployeeCsvRowError` for employees, extended with `nationalId`/`gender`/`dateOfBirth`).

- `POST /admin/employees/import` — accepts `.csv` or `.xlsx` (dispatched by extension). `EmployeeService.importEmployeesFromXlsx`/`importEmployeesFromCsv` share row-validation logic (`EmployeeService.runImport`) against a normalized `Map<String,String>` per row (headers matched case/punctuation-insensitively, e.g. `DateofBirth` → `dateofbirth`). Excel date-formatted cells are converted to ISO strings before validation.
- `POST /admin/livestock/import?year=YYYY` and `POST /admin/milk/import?year=YYYY` — new `BulkImportService`, `.xlsx` only. The template has no year column, so the admin picks one year per upload. Both importers resolve `Farm` by name and `Month` by 3-letter abbreviation, then **merge** into any existing `MonthlyReport` for that farm/month rather than blindly replacing — `upsertLivestock`/`upsertMilk` are destructive (delete-then-reinsert per report), so types/days not present in the uploaded columns (e.g. the `TOTALS` GOATS type, which has no template column) are read from the existing report and carried forward untouched.
- Livestock column headers are mapped to `livestock_types.type` via a small alias table for abbreviations that don't match 1:1 (`WHEIFERS`→`WEANERS_HEIFERS`, `WBULLS`→`WEANERS_BULLS`, `CHEIFERS`→`CALVES_HEIFERS`, `CBULLS`→`CALVES_BULLS`); everything else (`MILKING`, `DRY`, `MATERNITY`, `COMPOUND`, `SEGERO`, `HES`, `SHES`, `EWES`, `RAMS`, `BOARS`, `SOWS`, `PIGLETS`, `WEANERS`, `PORKERS`, `BACONERS`) matches the header text directly, keyed by `(category, type)` so there's no cross-category ambiguity. The template's two-row header (category group on row 1, type on row 2) is parsed dynamically (forward-filling merged category cells), not by hardcoded column position.
- A resolved-but-missing `LivestockType` for a given farm (e.g. a `PIGS` column on a farm that doesn't raise pigs) is a row-level validation error, not a silent skip — only genuinely blank cells are skipped.
- `POST /admin/employee-pay/import?startYear=YYYY&startMonth=MM` (`startMonth` defaults to 1) imports the `Employee pay_import` sheet — one row per calendar month, one Earned/Paid column pair per employee headed by their **bare** LS number (e.g. `LS2001`, without the farm-letter suffix `employees.ls_number` actually carries — resolved by stripping the trailing letter off each employee's real LS number and matching on the numeric base, since that base is globally unique per `V36`). The sheet has no year column and month labels repeat across years, so the caller-supplied `(startYear, startMonth)` anchors the first data row; every later row is assumed to be the next calendar month in sequence (wrapping year on Dec→Jan), cross-checked against its own Month label — a mismatch is a row error, not a silent reinterpretation.
  - **Writes to two independent tables, deliberately** — `payroll_entries` (Payroll tab) and `employee_payments` (Annual Ledger / employee "all-time paid" stat) have no relationship anywhere in the codebase; the Payroll tab reads `payroll_entries.amount_paid` while the Ledger reads `employee_payments` exclusively. So "Earned" writes to `payroll_entries.gross_salary`, and "Paid" writes to **both** `payroll_entries.amount_paid` (recomputing `amount_remaining = gross_salary - loans - amount_paid`) **and** a synthetic `employee_payments` row (dated the last day of that month) — otherwise one screen or the other would silently show stale/zero data for imported months.
  - The synthetic `employee_payments` rows are tagged with a fixed `note` marker (`BulkImportService.PAY_IMPORT_TAG`) so re-running the import for the same employee/month finds and replaces its own prior rows instead of accumulating duplicates or touching payments entered through the normal "Record Payment" UI.
  - Existing `payroll_entries` fields the sheet doesn't provide (`salary_rate`, `days_worked`, `loans`, `notes`) are left untouched on already-existing rows — only `gross_salary`/`amount_paid`/`amount_remaining` are overwritten.
