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
- The confirmed valid BCrypt hash for the default password `"changeme"` is `$2a$10$RlfO3Cgv2ulVh2B3gMcxGOz.hNiSyZfxJTz92b50IlSlYz8CSLlXe`. Use `HashGen.java` to generate hashes for new seed migrations.
- When adding new livestock categories, add them to the `LivestockCategory` enum AND insert the new `livestock_types` rows via a new migration.

### Migration history (latest first)
| Version | Description |
|---|---|
| V35 | Adds `gender VARCHAR(10)` (nullable) to `employees` table. |
| V34 | Employee profile — adds `ls_number` (global unique, starts LS2001), `date_of_birth`, `national_id` to `employees`; backfills LS numbers for all existing employees in ID order; creates `employee_payments` table for salaried staff payments. |
| V33 | Payroll seed data — Jan–May 2026 for all 5 farms (360 rows), extracted from Excel backups. Idempotent via ON CONFLICT DO UPDATE. Names matched via `UPPER(first_name)` on `employees` table. |
| V32 | Annual summary views: `summary_payroll`, `summary_milk_prod`, `summary_livestock` |
| V31 | `payroll_entries` table + `summary_payroll` view |
| V30 | (previous — check file for details) |
| V29 | Employee unification — merged `workers` into `employees` table; full name stored in `first_name` column (single field, no last_name for migrated workers) |

**Next migration must be V36.**
