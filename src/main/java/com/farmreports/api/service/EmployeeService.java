package com.farmreports.api.service;

import com.farmreports.api.dto.*;
import com.farmreports.api.entity.*;
import com.farmreports.api.repository.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepo;
    private final EmployeePaymentRepository paymentRepo;
    private final PayrollEntryRepository payrollRepo;
    private final DepartmentRepository departmentRepo;
    private final FarmRepository farmRepo;
    private final EmployeeIdService employeeIdService;
    private final JdbcTemplate jdbc;

    // ── Registry ──────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public PageDto<EmployeeDto> getEmployees(Integer farmId, Boolean isSalaried, Boolean isCasual, String search,
                                              String status, int page, int size) {
        Specification<Employee> spec = buildSpec(farmId, isSalaried, isCasual, search, status);
        Sort sort = Sort.by(Sort.Direction.ASC, "firstName").and(Sort.by(Sort.Direction.ASC, "lastName"));
        return toPageDto(employeeRepo.findAll(spec, PageRequest.of(page, size, sort)));
    }

    @Transactional(readOnly = true)
    public EmployeeDto getEmployee(Integer farmId, Integer id) {
        Employee emp = findOrThrow(farmId, id);
        return toDto(emp);
    }

    /** ADMIN-only master registry: every employee across every farm. */
    @Transactional(readOnly = true)
    public PageDto<EmployeeDto> getAllEmployeesAcrossFarms(Integer farmId, Boolean isSalaried, Boolean isCasual,
                                                             String search, int page, int size) {
        Specification<Employee> spec = buildSpec(farmId, isSalaried, isCasual, search, null);
        Sort sort = Sort.by(Sort.Direction.ASC, "farm.name")
                .and(Sort.by(Sort.Direction.ASC, "firstName"))
                .and(Sort.by(Sort.Direction.ASC, "lastName"));
        return toPageDto(employeeRepo.findAll(spec, PageRequest.of(page, size, sort)));
    }

    private static Specification<Employee> buildSpec(Integer farmId, Boolean isSalaried, Boolean isCasual,
                                                       String search, String status) {
        Specification<Employee> spec = Specification.where(null);
        if (farmId != null) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("farm").get("id"), farmId));
        }
        if (isSalaried != null) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("salaried"), isSalaried));
        }
        if (isCasual != null) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("casual"), isCasual));
        }
        if (status != null && !status.isBlank()) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("status"), status));
        }
        if (search != null && !search.isBlank()) {
            String like = "%" + search.trim().toLowerCase() + "%";
            spec = spec.and((root, q, cb) -> cb.or(
                    cb.like(cb.lower(root.get("firstName")), like),
                    cb.like(cb.lower(root.get("lastName")), like),
                    cb.like(cb.lower(root.get("lsNumber")), like)));
        }
        return spec;
    }

    private PageDto<EmployeeDto> toPageDto(Page<Employee> page) {
        List<EmployeeDto> content = page.getContent().stream().map(this::toDto).toList();
        return new PageDto<>(content, page.getTotalElements(), page.getTotalPages(), page.getNumber(), page.getSize());
    }

    @Transactional
    public EmployeeDto createEmployee(Integer farmId, EmployeeRequest request) {
        Farm farm = farmRepo.findById(farmId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Farm not found"));

        assertEmploymentTypeSelected(request.isSalaried(), request.isCasual());

        assertNotDuplicate(farm.getId(), farm.getName(), request.firstName(), request.lastName(), null);

        Employee emp = new Employee();
        emp.setFarm(farm);
        emp.setEmployeeId(employeeIdService.generateFor(farm.getName()));
        emp.setLsNumber(employeeIdService.generateLsNumber(farm.getName()));
        emp.setFirstName(request.firstName().trim());
        emp.setLastName(request.lastName() != null && !request.lastName().isBlank() ? request.lastName().trim() : null);
        emp.setPhone(request.phone() != null && !request.phone().isBlank() ? request.phone().trim() : null);
        emp.setSalaried(request.isSalaried());
        emp.setCasual(request.isCasual());
        emp.setJobTitle(request.jobTitle() != null && !request.jobTitle().isBlank() ? request.jobTitle().trim() : null);
        emp.setNationalId(request.nationalId() != null && !request.nationalId().isBlank() ? request.nationalId().trim() : null);
        emp.setGender(request.gender() != null && !request.gender().isBlank() ? request.gender().trim() : null);
        emp.setStartDate(parseDate(request.startDate()));
        emp.setDateOfBirth(parseDate(request.dateOfBirth()));
        emp.setDefaultDailyRate(request.defaultDailyRate());
        emp.setStatus("ACTIVE");

        if (request.departmentId() != null) {
            emp.setDepartment(departmentRepo.findByIdAndFarmId(request.departmentId(), farmId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Department not found")));
        }

        applyPhoto(emp, request.photoBase64(), request.photoMimeType());

        return toDto(employeeRepo.save(emp));
    }

    private static final int MAX_IMPORT_ROWS = 1000;
    private static final List<String> REQUIRED_IMPORT_COLUMNS = List.of("farmName", "firstName", "employmentType");

    // Normalized (lowercase, alphanumeric-only) field keys shared by the CSV and XLSX importers.
    private static final String F_FARM_NAME = "farmname";
    private static final String F_FIRST_NAME = "firstname";
    private static final String F_LAST_NAME = "lastname";
    private static final String F_PHONE = "phone";
    private static final String F_EMPLOYMENT_TYPE = "employmenttype";
    private static final String F_JOB_TITLE = "jobtitle";
    private static final String F_START_DATE = "startdate";
    private static final String F_DATE_OF_BIRTH = "dateofbirth";
    private static final String F_NATIONAL_ID = "nationalid";
    private static final String F_GENDER = "gender";
    private static final String F_DEFAULT_DAILY_RATE = "defaultdailyrate";

    /**
     * Validates every row first; if any row fails, nothing is inserted and every
     * row's error is returned. Only once all rows are clean does it insert them,
     * one {@link #createEmployee} call per row within this method's transaction.
     */
    @Transactional
    public EmployeeCsvImportResult importEmployeesFromCsv(MultipartFile file) {
        String content;
        try {
            content = new String(file.getBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not read CSV file");
        }
        if (!content.isEmpty() && content.charAt(0) == 0xFEFF) {
            content = content.substring(1);
        }

        CSVFormat format = CSVFormat.DEFAULT.builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .setIgnoreHeaderCase(true)
                .setTrim(true)
                .setIgnoreEmptyLines(true)
                .build();

        List<CSVRecord> records;
        Map<String, String> headerByNormalized;
        try (CSVParser parser = CSVParser.parse(new StringReader(content), format)) {
            headerByNormalized = parser.getHeaderNames().stream()
                    .collect(Collectors.toMap(EmployeeService::normalizeHeader, h -> h, (a, b) -> a));
            records = parser.getRecords();
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not parse CSV: " + e.getMessage());
        }

        assertRequiredColumnsPresent(headerByNormalized.keySet());
        if (records.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CSV file has no data rows");
        }
        if (records.size() > MAX_IMPORT_ROWS) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "CSV exceeds maximum of " + MAX_IMPORT_ROWS + " rows per import");
        }

        List<Map<String, String>> rows = new ArrayList<>();
        for (CSVRecord record : records) {
            Map<String, String> fields = new LinkedHashMap<>();
            for (Map.Entry<String, String> e : headerByNormalized.entrySet()) {
                String v = record.get(e.getValue());
                fields.put(e.getKey(), v != null && !v.isBlank() ? v.trim() : null);
            }
            rows.add(fields);
        }

        return runImport(rows);
    }

    /**
     * Same validate-then-insert contract as {@link #importEmployeesFromCsv}, but reads the
     * first sheet of an .xlsx workbook (e.g. the "employee_import_template" sheet) instead.
     */
    @Transactional
    public EmployeeCsvImportResult importEmployeesFromXlsx(MultipartFile file) {
        List<Map<String, String>> rows;
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(sheet.getFirstRowNum());
            if (headerRow == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "XLSX file has no header row");
            }

            Map<Integer, String> normalizedByColumn = new LinkedHashMap<>();
            for (Cell cell : headerRow) {
                String value = readCellAsString(cell);
                if (value != null && !value.isBlank()) {
                    normalizedByColumn.put(cell.getColumnIndex(), normalizeHeader(value));
                }
            }
            assertRequiredColumnsPresent(new java.util.HashSet<>(normalizedByColumn.values()));

            rows = new ArrayList<>();
            for (int r = headerRow.getRowNum() + 1; r <= sheet.getLastRowNum(); r++) {
                Row row = sheet.getRow(r);
                if (row == null || isRowBlank(row)) continue;

                Map<String, String> fields = new LinkedHashMap<>();
                for (Map.Entry<Integer, String> e : normalizedByColumn.entrySet()) {
                    Cell cell = row.getCell(e.getKey());
                    String value = readCellAsString(cell);
                    fields.put(e.getValue(), value != null && !value.isBlank() ? value.trim() : null);
                }
                rows.add(fields);
            }
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not read XLSX file: " + e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not parse XLSX file: " + e.getMessage());
        }

        if (rows.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "XLSX file has no data rows");
        }
        if (rows.size() > MAX_IMPORT_ROWS) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "XLSX exceeds maximum of " + MAX_IMPORT_ROWS + " rows per import");
        }

        return runImport(rows);
    }

    private void assertRequiredColumnsPresent(Set<String> normalizedHeaders) {
        List<String> missingColumns = REQUIRED_IMPORT_COLUMNS.stream()
                .filter(c -> !normalizedHeaders.contains(normalizeHeader(c)))
                .toList();
        if (!missingColumns.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "File is missing required column(s): " + String.join(", ", missingColumns));
        }
    }

    /**
     * Shared validate-all-then-apply-all logic used by both the CSV and XLSX importers. A row
     * whose (farm, first name, last name) matches an existing employee — or an earlier row in
     * the same file — but requests an employment type they don't already have merges into that
     * employee (OR's the flag in) instead of erroring; only a row that adds nothing new is a
     * real duplicate error.
     */
    private EmployeeCsvImportResult runImport(List<Map<String, String>> rows) {
        Map<String, Farm> farmsByName = farmRepo.findAll().stream()
                .collect(Collectors.toMap(f -> f.getName().trim().toLowerCase(), f -> f, (a, b) -> a));

        Map<String, Employee> existingByKey = employeeRepo.findAll().stream()
                .collect(Collectors.toMap(e -> dedupeKey(e.getFarm().getId(), e.getFirstName(), e.getLastName()),
                        e -> e, (a, b) -> a));

        List<EmployeeCsvRowError> errors = new ArrayList<>();
        Map<String, PendingEmployee> pendingByKey = new LinkedHashMap<>();
        int rowNum = 1;
        for (Map<String, String> fields : rows) {
            rowNum++;
            List<String> issues = new ArrayList<>();

            String farmName = fields.get(F_FARM_NAME);
            String firstName = fields.get(F_FIRST_NAME);
            String lastName = fields.get(F_LAST_NAME);
            String phone = fields.get(F_PHONE);
            String employmentTypeRaw = fields.get(F_EMPLOYMENT_TYPE);
            String jobTitle = fields.get(F_JOB_TITLE);
            String startDate = fields.get(F_START_DATE);
            String dateOfBirth = fields.get(F_DATE_OF_BIRTH);
            String nationalId = fields.get(F_NATIONAL_ID);
            String gender = fields.get(F_GENDER);
            String defaultDailyRateRaw = fields.get(F_DEFAULT_DAILY_RATE);

            Farm farm = null;
            if (farmName == null) {
                issues.add("Farm name is required");
            } else {
                farm = farmsByName.get(farmName.toLowerCase());
                if (farm == null) {
                    issues.add("Unknown farm: '" + farmName + "'");
                }
            }

            if (firstName == null) {
                issues.add("First name is required");
            }

            EmploymentFlags flags = null;
            if (employmentTypeRaw == null) {
                issues.add("Employment type is required");
            } else {
                try {
                    flags = parseEmploymentFlags(employmentTypeRaw);
                } catch (IllegalArgumentException e) {
                    issues.add("Invalid employmentType '" + employmentTypeRaw + "' (must be SALARIED, CASUAL, or BOTH)");
                }
            }

            if (startDate != null) {
                try {
                    LocalDate.parse(startDate);
                } catch (Exception e) {
                    issues.add("Invalid startDate '" + startDate + "' (expected yyyy-MM-dd)");
                }
            }

            if (dateOfBirth != null) {
                try {
                    LocalDate.parse(dateOfBirth);
                } catch (Exception e) {
                    issues.add("Invalid dateOfBirth '" + dateOfBirth + "' (expected yyyy-MM-dd)");
                }
            }

            BigDecimal defaultDailyRate = null;
            if (defaultDailyRateRaw != null) {
                try {
                    defaultDailyRate = new BigDecimal(defaultDailyRateRaw);
                } catch (NumberFormatException e) {
                    issues.add("Invalid defaultDailyRate '" + defaultDailyRateRaw + "'");
                }
            }

            String rowSummary = (farmName != null ? farmName : "?") + " / "
                    + (firstName != null ? firstName : "?") + (lastName != null ? " " + lastName : "");

            if (!issues.isEmpty()) {
                errors.add(new EmployeeCsvRowError(rowNum, rowSummary, String.join("; ", issues)));
                continue;
            }

            String key = dedupeKey(farm.getId(), firstName, lastName);
            Employee existing = existingByKey.get(key);
            PendingEmployee pending = pendingByKey.get(key);
            boolean addsSalaried = flags.salaried() && !(existing != null && existing.isSalaried())
                    && !(pending != null && pending.salaried);
            boolean addsCasual = flags.casual() && !(existing != null && existing.isCasual())
                    && !(pending != null && pending.casual);

            // A row for a brand-new hire that repeats an earlier row's employment type with
            // nothing new to add is a real duplicate-row mistake. A row matching an employee
            // who already exists in the DB is never an error, even without a new employment
            // type: it's how the client re-uploads the roster to backfill personal details
            // (phone, DOB, national ID, etc.) that a data cleanup may have wiped.
            if (existing == null && pending != null && !addsSalaried && !addsCasual) {
                errors.add(new EmployeeCsvRowError(rowNum, rowSummary,
                        "Duplicate row in file: " + firstName + (lastName != null ? " " + lastName : "")
                                + " on " + farm.getName() + " appears more than once"));
                continue;
            }

            if (pending == null) {
                pending = new PendingEmployee(existing, farm, firstName, lastName);
                pendingByKey.put(key, pending);
            }
            pending.salaried = pending.salaried || flags.salaried();
            pending.casual = pending.casual || flags.casual();
            // Non-identity fields: the last non-blank value in the file for this person wins,
            // so a blank cell on a later row never erases a value an earlier row provided.
            if (phone != null) pending.phone = phone;
            if (jobTitle != null) pending.jobTitle = jobTitle;
            if (startDate != null) pending.startDate = startDate;
            if (dateOfBirth != null) pending.dateOfBirth = dateOfBirth;
            if (nationalId != null) pending.nationalId = nationalId;
            if (gender != null) pending.gender = gender;
            if (defaultDailyRate != null) pending.defaultDailyRate = defaultDailyRate;
        }

        if (!errors.isEmpty()) {
            return new EmployeeCsvImportResult(false, rows.size(), 0, 0, errors);
        }

        int imported = 0;
        int merged = 0;
        for (PendingEmployee pending : pendingByKey.values()) {
            if (pending.existing == null) {
                EmployeeRequest request = new EmployeeRequest(
                        pending.firstName, pending.lastName, pending.phone, pending.salaried, pending.casual,
                        pending.jobTitle, null, pending.startDate, pending.dateOfBirth, pending.nationalId,
                        pending.gender, pending.defaultDailyRate, null, null, null);
                createEmployee(pending.farm.getId(), request);
                imported++;
            } else {
                Employee emp = pending.existing;
                emp.setSalaried(emp.isSalaried() || pending.salaried);
                emp.setCasual(emp.isCasual() || pending.casual);
                // Backfill only — never overwrite a field that already has a value, so a
                // repair upload can't clobber data that's already correct.
                if (isBlank(emp.getPhone()) && pending.phone != null) emp.setPhone(pending.phone);
                if (isBlank(emp.getJobTitle()) && pending.jobTitle != null) emp.setJobTitle(pending.jobTitle);
                if (emp.getStartDate() == null && pending.startDate != null) emp.setStartDate(parseDate(pending.startDate));
                if (emp.getDateOfBirth() == null && pending.dateOfBirth != null) emp.setDateOfBirth(parseDate(pending.dateOfBirth));
                if (isBlank(emp.getNationalId()) && pending.nationalId != null) emp.setNationalId(pending.nationalId);
                if (isBlank(emp.getGender()) && pending.gender != null) emp.setGender(pending.gender);
                if (emp.getDefaultDailyRate() == null && pending.defaultDailyRate != null) emp.setDefaultDailyRate(pending.defaultDailyRate);
                employeeRepo.save(emp);
                merged++;
            }
        }
        return new EmployeeCsvImportResult(true, rows.size(), imported, merged, List.of());
    }

    /** Accumulates one file's rows for a single (farm, name) identity across possibly multiple rows. */
    private static final class PendingEmployee {
        final Employee existing;
        final Farm farm;
        final String firstName;
        final String lastName;
        boolean salaried;
        boolean casual;
        String phone;
        String jobTitle;
        String startDate;
        String dateOfBirth;
        String nationalId;
        String gender;
        BigDecimal defaultDailyRate;

        PendingEmployee(Employee existing, Farm farm, String firstName, String lastName) {
            this.existing = existing;
            this.farm = farm;
            this.firstName = firstName;
            this.lastName = lastName;
        }
    }

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    private static String normalizeHeader(String s) {
        return s.toLowerCase().replaceAll("[^a-z0-9]", "");
    }

    private static String dedupeKey(Integer farmId, String firstName, String lastName) {
        String first = firstName.trim().toLowerCase();
        String last = lastName != null ? lastName.trim().toLowerCase() : "";
        return farmId + "|" + first + "|" + last;
    }

    /**
     * Guards createEmployee/updateEmployee the same way {@link #runImport} guards bulk import:
     * same farm + first + last name (case/whitespace-insensitive) is treated as the same person.
     * excludeEmployeeId lets an update pass against its own unchanged record.
     */
    private void assertNotDuplicate(Integer farmId, String farmName, String firstName, String lastName, Integer excludeEmployeeId) {
        if (employeeRepo.existsDuplicate(farmId, firstName, lastName, excludeEmployeeId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Employee already exists on " + farmName + ": "
                    + firstName + (lastName != null && !lastName.isBlank() ? " " + lastName : ""));
        }
    }

    private static boolean isRowBlank(Row row) {
        for (Cell cell : row) {
            if (readCellAsString(cell) != null) return false;
        }
        return true;
    }

    /** Reads any POI cell type as a trimmed string, converting date-formatted numeric cells to ISO yyyy-MM-dd. */
    private static String readCellAsString(Cell cell) {
        if (cell == null) return null;
        CellType type = cell.getCellType() == CellType.FORMULA ? cell.getCachedFormulaResultType() : cell.getCellType();
        String value = switch (type) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    yield cell.getLocalDateTimeCellValue().toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE);
                }
                double d = cell.getNumericCellValue();
                yield (d == Math.floor(d) && !Double.isInfinite(d))
                        ? String.valueOf((long) d)
                        : String.valueOf(d);
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> null;
        };
        return value != null && !value.isBlank() ? value.trim() : null;
    }

    @Transactional
    public EmployeeDto updateEmployee(Integer farmId, Integer id, EmployeeRequest request) {
        Employee emp = findOrThrow(farmId, id);

        assertNotDuplicate(farmId, emp.getFarm().getName(), request.firstName(), request.lastName(), id);
        assertEmploymentTypeSelected(request.isSalaried(), request.isCasual());

        emp.setFirstName(request.firstName().trim());
        emp.setLastName(request.lastName() != null && !request.lastName().isBlank() ? request.lastName().trim() : null);
        emp.setPhone(request.phone() != null && !request.phone().isBlank() ? request.phone().trim() : null);
        emp.setSalaried(request.isSalaried());
        emp.setCasual(request.isCasual());
        emp.setJobTitle(request.jobTitle() != null && !request.jobTitle().isBlank() ? request.jobTitle().trim() : null);
        emp.setNationalId(request.nationalId() != null && !request.nationalId().isBlank() ? request.nationalId().trim() : null);
        emp.setGender(request.gender() != null && !request.gender().isBlank() ? request.gender().trim() : null);
        emp.setStartDate(parseDate(request.startDate()));
        emp.setDateOfBirth(parseDate(request.dateOfBirth()));
        emp.setDefaultDailyRate(request.defaultDailyRate());

        if (request.status() != null) {
            if (!request.status().equals("ACTIVE") && !request.status().equals("INACTIVE")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status must be ACTIVE or INACTIVE");
            }
            emp.setStatus(request.status());
        }

        if (request.departmentId() != null) {
            emp.setDepartment(departmentRepo.findByIdAndFarmId(request.departmentId(), farmId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Department not found")));
        } else {
            emp.setDepartment(null);
        }

        if (request.photoBase64() != null && !request.photoBase64().isBlank()) {
            applyPhoto(emp, request.photoBase64(), request.photoMimeType());
        }

        return toDto(employeeRepo.save(emp));
    }

    @Transactional
    public EmployeeDto deactivateEmployee(Integer farmId, Integer id) {
        Employee emp = findOrThrow(farmId, id);
        emp.setStatus("INACTIVE");
        return toDto(employeeRepo.save(emp));
    }

    private static final String HISTORY_CHECK_SQL = """
            SELECT
              EXISTS(SELECT 1 FROM attendance WHERE worker_id = ?) OR
              EXISTS(SELECT 1 FROM attendance_worker_notes WHERE worker_id = ?) OR
              EXISTS(SELECT 1 FROM casual_attendance WHERE casual_labourer_id = ?) OR
              EXISTS(SELECT 1 FROM casual_work_entries WHERE casual_labourer_id = ?) OR
              EXISTS(SELECT 1 FROM casual_labourer_payments WHERE casual_labourer_id = ?) OR
              EXISTS(SELECT 1 FROM payroll_entries WHERE employee_id = ?) OR
              EXISTS(SELECT 1 FROM employee_payments WHERE employee_id = ?)
            """;

    /**
     * Hard-deletes an employee — only when they have zero recorded history (attendance,
     * payroll, casual work, payments). Every one of those tables has a FK straight to
     * employees(id) with no cascade, so a real hard delete of an employee with history
     * would fail at the DB level anyway; this checks up front to give a clear error
     * instead. Employees who've actually worked should be deactivated, not deleted.
     */
    @Transactional
    public void deleteEmployee(Integer farmId, Integer id) {
        Employee emp = findOrThrow(farmId, id);
        Integer eid = emp.getId();
        Boolean hasHistory = jdbc.queryForObject(HISTORY_CHECK_SQL, Boolean.class,
                eid, eid, eid, eid, eid, eid, eid);
        if (Boolean.TRUE.equals(hasHistory)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    emp.getFullName() + " has recorded attendance, payroll, or payment history and can't be deleted — deactivate them instead.");
        }
        employeeRepo.delete(emp);
    }

    // ── Salaried payments ─────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public EmployeeSummaryDto getSummary(Integer farmId, Integer employeeId) {
        findOrThrow(farmId, employeeId);

        BigDecimal allTimeEarned = jdbc.queryForObject(
                "SELECT COALESCE(SUM(gross_salary), 0) FROM payroll_entries WHERE farm_id = ? AND employee_id = ?",
                BigDecimal.class, farmId, employeeId);
        BigDecimal allTimePaid = paymentRepo.sumAmountByEmployeeIdAndFarmId(employeeId, farmId);

        BigDecimal outstanding = (allTimeEarned != null ? allTimeEarned : BigDecimal.ZERO)
                .subtract(allTimePaid != null ? allTimePaid : BigDecimal.ZERO);

        List<EmployeePaymentDto> payments = paymentRepo
                .findByEmployeeIdAndFarmIdOrderByPaymentDateDesc(employeeId, farmId)
                .stream().map(this::toPaymentDto).toList();

        return new EmployeeSummaryDto(
                allTimeEarned != null ? allTimeEarned : BigDecimal.ZERO,
                allTimePaid != null ? allTimePaid : BigDecimal.ZERO,
                outstanding,
                payments
        );
    }

    /** Annual payroll for every salaried employee on a farm — the "Payroll" tab on a report's
     *  detail page reuses this rather than an attendance-tied view, tracking each employee's
     *  earnings/payments for the whole year, not just the report's own month. */
    @Transactional(readOnly = true)
    public List<EmployeeAnnualPayrollDto> getFarmAnnualPayroll(Integer farmId, Integer year) {
        return employeeRepo.findByFarmIdAndSalariedTrueOrderByFirstNameAscLastNameAsc(farmId).stream()
                .map(emp -> new EmployeeAnnualPayrollDto(
                        emp.getId(),
                        emp.getFullName(),
                        emp.getLsNumber(),
                        emp.getStatus(),
                        getEmployeeLedger(farmId, emp.getId(), year)))
                .toList();
    }

    @Transactional(readOnly = true)
    public EmployeeLedgerDto getEmployeeLedger(Integer farmId, Integer employeeId, Integer year) {
        findOrThrow(farmId, employeeId);

        BigDecimal openingBalance = payrollRepo.sumGrossSalaryBeforeYear(farmId, employeeId, year)
                .subtract(paymentRepo.sumAmountBeforeDate(employeeId, farmId, LocalDate.of(year, 1, 1)));

        Map<Integer, BigDecimal> earnedByMonth = payrollRepo.findByFarmIdAndEmployeeIdAndYear(farmId, employeeId, year).stream()
                .collect(Collectors.toMap(PayrollEntry::getMonth,
                        e -> e.getGrossSalary() != null ? e.getGrossSalary() : BigDecimal.ZERO, BigDecimal::add));
        Map<Integer, BigDecimal> paidByMonth = paymentRepo.findByEmployeeIdAndFarmIdAndPaymentDateBetween(
                        employeeId, farmId, LocalDate.of(year, 1, 1), LocalDate.of(year, 12, 31)).stream()
                .collect(Collectors.groupingBy(p -> p.getPaymentDate().getMonthValue(),
                        Collectors.reducing(BigDecimal.ZERO, EmployeePayment::getAmount, BigDecimal::add)));

        List<EmployeeLedgerMonthDto> months = new ArrayList<>();
        BigDecimal running = openingBalance;
        BigDecimal totalEarned = BigDecimal.ZERO;
        BigDecimal totalPaid = BigDecimal.ZERO;
        for (int m = 1; m <= 12; m++) {
            BigDecimal earned = earnedByMonth.getOrDefault(m, BigDecimal.ZERO);
            BigDecimal paid = paidByMonth.getOrDefault(m, BigDecimal.ZERO);
            running = running.add(earned).subtract(paid);
            totalEarned = totalEarned.add(earned);
            totalPaid = totalPaid.add(paid);
            months.add(new EmployeeLedgerMonthDto(m, earned, paid, running));
        }

        return new EmployeeLedgerDto(year, openingBalance, totalEarned, totalPaid, running, months);
    }

    @Transactional
    public EmployeePaymentDto recordPayment(Integer farmId, Integer employeeId,
                                            RecordPaymentRequest request, String paidBy) {
        Employee emp = findOrThrow(farmId, employeeId);

        EmployeePayment payment = new EmployeePayment();
        payment.setEmployeeId(employeeId);
        payment.setFarmId(farmId);
        payment.setPaymentDate(request.paymentDate());
        payment.setAmount(request.amount());
        payment.setNote(request.note());
        payment.setPaidBy(paidBy);

        return toPaymentDto(paymentRepo.save(payment));
    }

    @Transactional
    public void deletePayment(Integer farmId, Integer employeeId, Integer paymentId) {
        findOrThrow(farmId, employeeId);
        EmployeePayment payment = paymentRepo.findByIdAndFarmId(paymentId, farmId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found"));
        paymentRepo.delete(payment);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Employee findOrThrow(Integer farmId, Integer id) {
        return employeeRepo.findByIdAndFarmId(id, farmId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found"));
    }

    private static void assertEmploymentTypeSelected(boolean isSalaried, boolean isCasual) {
        if (!isSalaried && !isCasual) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Employee must be salaried, casual, or both");
        }
    }

    /** Parses the import file's single employmentType column value into the two flags. */
    private static EmploymentFlags parseEmploymentFlags(String raw) {
        return switch (raw.toUpperCase()) {
            case "SALARIED" -> new EmploymentFlags(true, false);
            case "CASUAL" -> new EmploymentFlags(false, true);
            case "BOTH" -> new EmploymentFlags(true, true);
            default -> throw new IllegalArgumentException("Invalid employmentType: " + raw);
        };
    }

    private record EmploymentFlags(boolean salaried, boolean casual) {}

    private static LocalDate parseDate(String s) {
        if (s == null || s.isBlank()) return null;
        try {
            return LocalDate.parse(s);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid date: " + s);
        }
    }

    private static void applyPhoto(Employee emp, String photoBase64, String photoMimeType) {
        if (photoBase64 != null && !photoBase64.isBlank()) {
            emp.setPhotoData(Base64.getDecoder().decode(photoBase64));
            emp.setPhotoMimeType(photoMimeType != null ? photoMimeType : "image/jpeg");
        }
    }

    EmployeeDto toDto(Employee e) {
        String photoBase64 = null;
        if (e.getPhotoData() != null) {
            photoBase64 = Base64.getEncoder().encodeToString(e.getPhotoData());
        }
        return new EmployeeDto(
                e.getId(),
                e.getFarm().getId(),
                e.getFarm().getName(),
                e.getLsNumber(),
                e.getEmployeeId(),
                e.getFirstName(),
                e.getLastName(),
                e.getFullName(),
                e.getPhone(),
                e.isSalaried(),
                e.isCasual(),
                e.getJobTitle(),
                e.getDepartment() != null ? e.getDepartment().getName() : null,
                e.getStartDate() != null ? e.getStartDate().toString() : null,
                e.getDateOfBirth() != null ? e.getDateOfBirth().toString() : null,
                e.getNationalId(),
                e.getGender(),
                e.getAge(),
                e.getStatus(),
                e.getDefaultDailyRate(),
                photoBase64,
                e.getPhotoMimeType()
        );
    }

    private EmployeePaymentDto toPaymentDto(EmployeePayment p) {
        Employee emp = employeeRepo.findById(p.getEmployeeId()).orElse(null);
        String name = emp != null ? emp.getFullName() : "Unknown";
        return new EmployeePaymentDto(
                p.getId(),
                p.getEmployeeId(),
                name,
                p.getPaymentDate(),
                p.getAmount(),
                p.getNote(),
                p.getPaidBy(),
                p.getCreatedAt()
        );
    }
}
