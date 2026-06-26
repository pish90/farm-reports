package com.farmreports.api.service;

import com.farmreports.api.dto.*;
import com.farmreports.api.entity.*;
import com.farmreports.api.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepo;
    private final EmployeePaymentRepository paymentRepo;
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

    @Transactional
    public EmployeeDto createEmployee(Integer farmId, EmployeeRequest request) {
        Farm farm = farmRepo.findById(farmId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Farm not found"));

        EmploymentType type = parseType(request.employmentType());

        Employee emp = new Employee();
        emp.setFarm(farm);
        emp.setEmployeeId(employeeIdService.generateFor(farm.getName()));
        emp.setLsNumber(employeeIdService.generateLsNumber());
        emp.setFirstName(request.firstName().trim());
        emp.setLastName(request.lastName() != null && !request.lastName().isBlank() ? request.lastName().trim() : null);
        emp.setPhone(request.phone() != null && !request.phone().isBlank() ? request.phone().trim() : null);
        emp.setEmploymentType(type);
        emp.setJobTitle(request.jobTitle() != null && !request.jobTitle().isBlank() ? request.jobTitle().trim() : null);
        emp.setNationalId(request.nationalId() != null && !request.nationalId().isBlank() ? request.nationalId().trim() : null);
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

    @Transactional
    public EmployeeDto updateEmployee(Integer farmId, Integer id, EmployeeRequest request) {
        Employee emp = findOrThrow(farmId, id);

        emp.setFirstName(request.firstName().trim());
        emp.setLastName(request.lastName() != null && !request.lastName().isBlank() ? request.lastName().trim() : null);
        emp.setPhone(request.phone() != null && !request.phone().isBlank() ? request.phone().trim() : null);
        emp.setJobTitle(request.jobTitle() != null && !request.jobTitle().isBlank() ? request.jobTitle().trim() : null);
        emp.setNationalId(request.nationalId() != null && !request.nationalId().isBlank() ? request.nationalId().trim() : null);
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
