package com.farmreports.api.service;

import com.farmreports.api.dto.*;
import com.farmreports.api.entity.*;
import com.farmreports.api.repository.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
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
import java.util.ArrayList;
import java.util.Base64;
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
    public List<EmployeeDto> getEmployees(Integer farmId, String employmentType, String search) {
        List<Employee> employees;
        if (search != null && !search.isBlank()) {
            employees = employeeRepo.searchByFarmId(farmId, search.trim());
            if (employmentType != null) {
                EmploymentType type = parseType(employmentType);
                employees = employees.stream().filter(e -> e.getEmploymentType() == type).toList();
            }
        } else if (employmentType != null) {
            employees = employeeRepo.findByFarmIdAndEmploymentTypeOrderByFirstNameAscLastNameAsc(
                    farmId, parseType(employmentType));
        } else {
            employees = employeeRepo.findByFarmIdOrderByFirstNameAscLastNameAsc(farmId);
        }
        return employees.stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public EmployeeDto getEmployee(Integer farmId, Integer id) {
        Employee emp = findOrThrow(farmId, id);
        return toDto(emp);
    }

    /** ADMIN-only master registry: every employee across every farm. */
    @Transactional(readOnly = true)
    public List<EmployeeDto> getAllEmployeesAcrossFarms() {
        return employeeRepo.findAllOrderByFarmAndName()
                .stream().map(this::toDto).toList();
    }

    @Transactional
    public EmployeeDto createEmployee(Integer farmId, EmployeeRequest request) {
        Farm farm = farmRepo.findById(farmId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Farm not found"));

        EmploymentType type = parseType(request.employmentType());

        Employee emp = new Employee();
        emp.setFarm(farm);
        emp.setEmployeeId(employeeIdService.generateFor(farm.getName()));
        emp.setLsNumber(employeeIdService.generateLsNumber(farm.getName()));
        emp.setFirstName(request.firstName().trim());
        emp.setLastName(request.lastName() != null && !request.lastName().isBlank() ? request.lastName().trim() : null);
        emp.setPhone(request.phone() != null && !request.phone().isBlank() ? request.phone().trim() : null);
        emp.setEmploymentType(type);
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
        Set<String> presentHeaders;
        try (CSVParser parser = CSVParser.parse(new StringReader(content), format)) {
            presentHeaders = parser.getHeaderNames().stream().map(String::toLowerCase).collect(Collectors.toSet());
            records = parser.getRecords();
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not parse CSV: " + e.getMessage());
        }

        List<String> missingColumns = REQUIRED_IMPORT_COLUMNS.stream()
                .filter(c -> !presentHeaders.contains(c.toLowerCase()))
                .toList();
        if (!missingColumns.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "CSV is missing required column(s): " + String.join(", ", missingColumns));
        }
        if (records.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CSV file has no data rows");
        }
        if (records.size() > MAX_IMPORT_ROWS) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "CSV exceeds maximum of " + MAX_IMPORT_ROWS + " rows per import");
        }

        Map<String, Farm> farmsByName = farmRepo.findAll().stream()
                .collect(Collectors.toMap(f -> f.getName().trim().toLowerCase(), f -> f, (a, b) -> a));

        List<EmployeeCsvRowError> errors = new ArrayList<>();
        List<ValidImportRow> validRows = new ArrayList<>();
        int rowNum = 1;
        for (CSVRecord record : records) {
            rowNum++;
            List<String> issues = new ArrayList<>();

            String farmName = getField(record, presentHeaders, "farmName");
            String firstName = getField(record, presentHeaders, "firstName");
            String lastName = getField(record, presentHeaders, "lastName");
            String phone = getField(record, presentHeaders, "phone");
            String employmentTypeRaw = getField(record, presentHeaders, "employmentType");
            String jobTitle = getField(record, presentHeaders, "jobTitle");
            String startDate = getField(record, presentHeaders, "startDate");
            String defaultDailyRateRaw = getField(record, presentHeaders, "defaultDailyRate");

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

            EmploymentType employmentType = null;
            if (employmentTypeRaw == null) {
                issues.add("Employment type is required");
            } else {
                try {
                    employmentType = EmploymentType.valueOf(employmentTypeRaw.toUpperCase());
                } catch (IllegalArgumentException e) {
                    issues.add("Invalid employmentType '" + employmentTypeRaw + "' (must be SALARIED or CASUAL)");
                }
            }

            if (startDate != null) {
                try {
                    LocalDate.parse(startDate);
                } catch (Exception e) {
                    issues.add("Invalid startDate '" + startDate + "' (expected yyyy-MM-dd)");
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

            EmployeeRequest request = new EmployeeRequest(
                    firstName, lastName, phone, employmentType.name(), jobTitle,
                    null, startDate, null, null, null,
                    defaultDailyRate, null, null, null);
            validRows.add(new ValidImportRow(farm.getId(), request));
        }

        if (!errors.isEmpty()) {
            return new EmployeeCsvImportResult(false, records.size(), 0, errors);
        }

        int imported = 0;
        for (ValidImportRow row : validRows) {
            createEmployee(row.farmId(), row.request());
            imported++;
        }
        return new EmployeeCsvImportResult(true, records.size(), imported, List.of());
    }

    private record ValidImportRow(Integer farmId, EmployeeRequest request) {}

    private static String getField(CSVRecord record, Set<String> presentHeaders, String name) {
        if (!presentHeaders.contains(name.toLowerCase())) return null;
        String v = record.get(name);
        return v != null && !v.isBlank() ? v : null;
    }

    @Transactional
    public EmployeeDto updateEmployee(Integer farmId, Integer id, EmployeeRequest request) {
        Employee emp = findOrThrow(farmId, id);

        emp.setFirstName(request.firstName().trim());
        emp.setLastName(request.lastName() != null && !request.lastName().isBlank() ? request.lastName().trim() : null);
        emp.setPhone(request.phone() != null && !request.phone().isBlank() ? request.phone().trim() : null);
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

    private static EmploymentType parseType(String s) {
        try {
            return EmploymentType.valueOf(s.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid employmentType: " + s);
        }
    }

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
                e.getEmploymentType().name(),
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
