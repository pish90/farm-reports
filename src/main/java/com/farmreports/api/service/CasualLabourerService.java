package com.farmreports.api.service;

import com.farmreports.api.dto.*;
import com.farmreports.api.entity.*;
import com.farmreports.api.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CasualLabourerService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final CasualLabourerPaymentRepository paymentRepository;
    private final CasualWorkSessionRepository workSessionRepository;
    private final CasualWorkEntryRepository workEntryRepository;
    private final FarmRepository farmRepository;
    private final MonthlyReportRepository monthlyReportRepository;
    private final CasualAttendanceRepository casualAttendanceRepository;
    private final EmployeeIdService employeeIdService;
    private final ExportService exportService;

    // ── Casual labourers ─────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<CasualLabourerDto> getActiveCasualLabourers(Integer farmId) {
        return employeeRepository
                .findByFarmIdAndStatusAndCasualTrue(farmId, "ACTIVE")
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public CasualLabourerDto addCasualLabourer(Integer farmId, CasualLabourerRequest request) {
        Farm farm = farmRepository.findById(farmId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Farm not found"));

        String firstName = resolveFirstName(request.firstName(), request.name());
        if (firstName == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "First name is required");
        }

        Employee employee = new Employee();
        employee.setFarm(farm);
        employee.setEmployeeId(employeeIdService.generateFor(farm.getName()));
        employee.setLsNumber(employeeIdService.generateLsNumber(farm.getName()));
        employee.setFirstName(firstName);
        employee.setLastName(resolveLastName(request.firstName(), request.lastName(), request.name()));
        employee.setPhone(request.phone() != null && !request.phone().isBlank() ? request.phone().trim() : null);
        employee.setSalaried(false);
        employee.setCasual(true);
        employee.setJobTitle(request.jobTitle());
        employee.setStartDate(request.startDate() != null ? LocalDate.parse(request.startDate()) : null);
        employee.setStatus("ACTIVE");

        if (request.departmentId() != null) {
            employee.setDepartment(departmentRepository.findByIdAndFarmId(request.departmentId(), farmId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Department not found")));
        }

        if (request.photoBase64() != null && !request.photoBase64().isBlank()) {
            employee.setPhotoData(Base64.getDecoder().decode(request.photoBase64()));
            employee.setPhotoMimeType(request.photoMimeType() != null ? request.photoMimeType() : "image/jpeg");
        }

        return toDto(employeeRepository.save(employee));
    }

    @Transactional
    public CasualLabourerDto updateCasualLabourer(Integer farmId, Integer labourerId, CasualLabourerRequest request) {
        Employee employee = employeeRepository.findByIdAndFarmIdAndCasualTrue(labourerId, farmId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Casual labourer not found"));

        String firstName = resolveFirstName(request.firstName(), request.name());
        if (firstName != null) employee.setFirstName(firstName);
        if (request.lastName() != null) employee.setLastName(request.lastName().isBlank() ? null : request.lastName().trim());
        if (request.phone() != null) employee.setPhone(request.phone().isBlank() ? null : request.phone().trim());
        if (request.jobTitle() != null) employee.setJobTitle(request.jobTitle().isBlank() ? null : request.jobTitle());
        if (request.startDate() != null) employee.setStartDate(LocalDate.parse(request.startDate()));

        if (request.departmentId() != null) {
            employee.setDepartment(departmentRepository.findByIdAndFarmId(request.departmentId(), farmId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Department not found")));
        }

        if (request.photoBase64() != null && !request.photoBase64().isBlank()) {
            employee.setPhotoData(Base64.getDecoder().decode(request.photoBase64()));
            employee.setPhotoMimeType(request.photoMimeType() != null ? request.photoMimeType() : "image/jpeg");
        }

        return toDto(employeeRepository.save(employee));
    }

    @Transactional
    public void deactivateCasualLabourer(Integer farmId, Integer labourerId) {
        Employee employee = employeeRepository.findByIdAndFarmIdAndCasualTrue(labourerId, farmId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Casual labourer not found"));
        employee.setStatus("INACTIVE");
    }

    // ── Work Sessions ─────────────────────────────────────────────────────────

    @Transactional
    public CasualWorkSessionDto createWorkSession(Integer farmId, CreateWorkSessionRequest request) {
        Farm farm = farmRepository.findById(farmId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Farm not found"));

        CasualWorkSession session = new CasualWorkSession();
        session.setFarm(farm);
        session.setSessionDate(request.sessionDate());
        session.setActivity(request.activity().trim());
        session.setDefaultDailyRate(request.defaultDailyRate());

        for (WorkSessionEntryRequest er : request.entries()) {
            Employee employee = employeeRepository.findByIdAndFarmId(er.casualLabourerId(), farmId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Employee not found: " + er.casualLabourerId()));
            CasualWorkEntry entry = new CasualWorkEntry();
            entry.setSession(session);
            entry.setEmployee(employee);
            entry.setRateOverride(er.rateOverride());
            session.getEntries().add(entry);
        }

        return toSessionDto(workSessionRepository.save(session));
    }

    @Transactional
    public CasualWorkSessionDto updateWorkSession(Integer farmId, Integer sessionId, CreateWorkSessionRequest request) {
        CasualWorkSession session = workSessionRepository.findByIdAndFarmId(sessionId, farmId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Work session not found"));

        session.setSessionDate(request.sessionDate());
        session.setActivity(request.activity().trim());
        session.setDefaultDailyRate(request.defaultDailyRate());

        session.getEntries().clear();
        for (WorkSessionEntryRequest er : request.entries()) {
            Employee employee = employeeRepository.findByIdAndFarmId(er.casualLabourerId(), farmId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Employee not found: " + er.casualLabourerId()));
            CasualWorkEntry entry = new CasualWorkEntry();
            entry.setSession(session);
            entry.setEmployee(employee);
            entry.setRateOverride(er.rateOverride());
            session.getEntries().add(entry);
        }

        return toSessionDto(workSessionRepository.save(session));
    }

    @Transactional(readOnly = true)
    public PageDto<CasualWorkSessionDto> getWorkSessions(Integer farmId, Integer year, Integer month,
                                                          int page, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "sessionDate");
        PageRequest pageable = PageRequest.of(page, size, sort);

        Page<CasualWorkSession> result;
        if (year != null && month != null) {
            YearMonth ym = YearMonth.of(year, month);
            result = workSessionRepository.findByFarmIdAndSessionDateBetween(
                    farmId, ym.atDay(1), ym.atEndOfMonth(), pageable);
        } else {
            result = workSessionRepository.findByFarmId(farmId, pageable);
        }

        List<CasualWorkSessionDto> content = result.getContent().stream().map(this::toSessionDto).toList();
        return new PageDto<>(content, result.getTotalElements(), result.getTotalPages(), page, size);
    }

    @Transactional
    public void deleteWorkSession(Integer farmId, Integer sessionId) {
        CasualWorkSession session = workSessionRepository.findByIdAndFarmId(sessionId, farmId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Work session not found"));
        workSessionRepository.delete(session);
    }

    // ── All-Summaries Report ──────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<CasualLabourerReportDto> getAllSummaries(Integer farmId) {
        List<Employee> labourers = employeeRepository
                .findByFarmIdAndStatusAndCasualTrue(farmId, "ACTIVE");
        List<CasualWorkSession> sessions = workSessionRepository.findByFarmIdWithEntries(farmId);

        return labourers.stream().map(l -> {
            BigDecimal earned  = coalesce(workEntryRepository.sumEarnedByEmployeeId(l.getId()));
            BigDecimal paid    = coalesce(paymentRepository.sumAmountByEmployeeId(l.getId()));
            BigDecimal balance = earned.subtract(paid);

            List<CasualLabourerReportDto.WorkEntryLine> lines = sessions.stream()
                    .flatMap(s -> s.getEntries().stream()
                            .filter(e -> e.getEmployee().getId().equals(l.getId()))
                            .map(e -> new CasualLabourerReportDto.WorkEntryLine(
                                    s.getId(), s.getSessionDate(), s.getActivity(), e.effectiveRate())))
                    .toList();

            String photoBase64 = l.getPhotoData() != null
                    ? Base64.getEncoder().encodeToString(l.getPhotoData()) : null;

            return new CasualLabourerReportDto(
                    l.getId(), l.getFullName(), l.getPhone(),
                    photoBase64, l.getPhotoMimeType(),
                    earned, paid, balance, lines);
        }).toList();
    }

    // ── Annual ledger ─────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public EmployeeLedgerDto getLedger(Integer farmId, Integer labourerId, Integer year) {
        employeeRepository.findByIdAndFarmIdAndCasualTrue(labourerId, farmId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Casual labourer not found"));

        LocalDate yearStart = LocalDate.of(year, 1, 1);

        List<CasualWorkSession> sessions = workSessionRepository.findByFarmIdWithEntries(farmId);
        BigDecimal openingEarned = BigDecimal.ZERO;
        BigDecimal[] earnedByMonth = new BigDecimal[13];
        java.util.Arrays.fill(earnedByMonth, BigDecimal.ZERO);
        for (CasualWorkSession session : sessions) {
            for (CasualWorkEntry entry : session.getEntries()) {
                if (!entry.getEmployee().getId().equals(labourerId)) continue;
                LocalDate date = session.getSessionDate();
                BigDecimal rate = entry.effectiveRate();
                if (date.isBefore(yearStart)) {
                    openingEarned = openingEarned.add(rate);
                } else if (date.getYear() == year) {
                    int m = date.getMonthValue();
                    earnedByMonth[m] = earnedByMonth[m].add(rate);
                }
            }
        }

        List<CasualLabourerPayment> payments = paymentRepository.findByEmployeeIdOrderByPaymentDateDesc(labourerId);
        BigDecimal openingPaid = BigDecimal.ZERO;
        BigDecimal[] paidByMonth = new BigDecimal[13];
        java.util.Arrays.fill(paidByMonth, BigDecimal.ZERO);
        for (CasualLabourerPayment payment : payments) {
            LocalDate date = payment.getPaymentDate();
            if (date.isBefore(yearStart)) {
                openingPaid = openingPaid.add(payment.getAmount());
            } else if (date.getYear() == year) {
                int m = date.getMonthValue();
                paidByMonth[m] = paidByMonth[m].add(payment.getAmount());
            }
        }

        BigDecimal openingBalance = openingEarned.subtract(openingPaid);
        List<EmployeeLedgerMonthDto> months = new java.util.ArrayList<>();
        BigDecimal running = openingBalance;
        BigDecimal totalEarned = BigDecimal.ZERO;
        BigDecimal totalPaid = BigDecimal.ZERO;
        for (int m = 1; m <= 12; m++) {
            BigDecimal earned = earnedByMonth[m];
            BigDecimal paid = paidByMonth[m];
            running = running.add(earned).subtract(paid);
            totalEarned = totalEarned.add(earned);
            totalPaid = totalPaid.add(paid);
            months.add(new EmployeeLedgerMonthDto(m, earned, paid, running));
        }

        return new EmployeeLedgerDto(year, openingBalance, totalEarned, totalPaid, running, months);
    }

    // ── Per-labourer summary ─────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public CasualLabourerSummaryDto getSummary(Integer farmId, Integer labourerId) {
        employeeRepository.findByIdAndFarmIdAndCasualTrue(labourerId, farmId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Casual labourer not found"));

        BigDecimal allTimeEarned = coalesce(workEntryRepository.sumEarnedByEmployeeId(labourerId));
        BigDecimal allTimePaid   = coalesce(paymentRepository.sumAmountByEmployeeId(labourerId));
        BigDecimal outstanding   = allTimeEarned.subtract(allTimePaid);

        List<CasualLabourerPaymentDto> payments = paymentRepository
                .findByEmployeeIdOrderByPaymentDateDesc(labourerId)
                .stream()
                .map(this::toPaymentDto)
                .toList();

        return new CasualLabourerSummaryDto(allTimeEarned, allTimePaid, outstanding, payments);
    }

    // ── Payments ──────────────────────────────────────────────────────────────

    @Transactional
    public CasualLabourerPaymentDto recordPayment(Integer farmId, Integer labourerId,
                                                   RecordPaymentRequest request, String paidBy) {
        Employee employee = employeeRepository.findByIdAndFarmIdAndCasualTrue(
                        labourerId, farmId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Casual labourer not found"));
        Farm farm = farmRepository.getReferenceById(farmId);

        // Balance check: payment must not exceed total earned minus already paid
        BigDecimal earned    = coalesce(workEntryRepository.sumEarnedByEmployeeId(labourerId));
        BigDecimal alreadyPaid = coalesce(paymentRepository.sumAmountByEmployeeId(labourerId));
        BigDecimal available = earned.subtract(alreadyPaid);

        if (request.amount().compareTo(available) > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Payment of Ksh " + request.amount().toPlainString()
                    + " exceeds available balance of Ksh " + available.toPlainString()
                    + " (earned " + earned.toPlainString()
                    + ", already paid " + alreadyPaid.toPlainString() + ")");
        }

        CasualLabourerPayment payment = new CasualLabourerPayment();
        payment.setEmployee(employee);
        payment.setFarm(farm);
        payment.setPaymentDate(request.paymentDate());
        payment.setAmount(request.amount());
        payment.setNote(request.note());
        payment.setPaidBy(paidBy);

        return toPaymentDto(paymentRepository.save(payment));
    }

    @Transactional
    public void deletePayment(Integer farmId, Integer labourerId, Integer paymentId) {
        CasualLabourerPayment payment = paymentRepository.findByIdAndFarmId(paymentId, farmId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found"));
        if (!payment.getEmployee().getId().equals(labourerId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found");
        }
        paymentRepository.delete(payment);
    }

    // ── Monthly payroll ───────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<CasualPayrollEntryDto> getMonthlyPayroll(Integer farmId, Integer year, Integer month) {
        List<Employee> labourers = employeeRepository
                .findByFarmIdAndStatusAndCasualTrue(farmId, "ACTIVE");

        Integer reportId = getReportIdForMonth(farmId, year, month);
        var monthAttendance = reportId != null
                ? casualAttendanceRepository.findByReportId(reportId)
                : List.<CasualAttendance>of();

        return labourers.stream().map(l -> {
            var mine = monthAttendance.stream()
                    .filter(ca -> ca.getEmployee().getId().equals(l.getId()) && ca.isPresent())
                    .toList();

            BigDecimal monthEarnings = mine.stream()
                    .map(ca -> ca.getRateOverride() != null ? ca.getRateOverride() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal allTimePaid   = coalesce(paymentRepository.sumAmountByEmployeeId(l.getId()));
            BigDecimal allTimeEarned = coalesce(workEntryRepository.sumEarnedByEmployeeId(l.getId()));
            BigDecimal outstanding   = allTimeEarned.subtract(allTimePaid);

            String photoBase64 = l.getPhotoData() != null
                    ? Base64.getEncoder().encodeToString(l.getPhotoData()) : null;

            return new CasualPayrollEntryDto(
                    l.getId(), l.getFullName(), l.getPhone(),
                    photoBase64, l.getPhotoMimeType(),
                    mine.size(), monthEarnings, allTimePaid, outstanding);
        }).toList();
    }

    // ── Export ────────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public byte[] generateMonthlyExcel(Integer farmId, Integer year, Integer month) {
        List<Employee> labourers = employeeRepository
                .findByFarmIdAndStatusAndCasualTrue(farmId, "ACTIVE");
        List<CasualWorkSession> sessions = workSessionRepository.findByFarmIdWithEntries(farmId);

        List<ExportService.CasualLabourerRow> summaryRows = labourers.stream().map(l -> {
            BigDecimal allTimeEarned = coalesce(workEntryRepository.sumEarnedByEmployeeId(l.getId()));
            BigDecimal allTimePaid   = coalesce(paymentRepository.sumAmountByEmployeeId(l.getId()));
            BigDecimal outstanding   = allTimeEarned.subtract(allTimePaid);

            BigDecimal monthEarned = sessions.stream()
                    .filter(s -> s.getSessionDate().getYear() == year && s.getSessionDate().getMonthValue() == month)
                    .flatMap(s -> s.getEntries().stream()
                            .filter(e -> e.getEmployee().getId().equals(l.getId())))
                    .map(CasualWorkEntry::effectiveRate)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            return new ExportService.CasualLabourerRow(l.getFullName(), l.getPhone(), monthEarned, allTimePaid, outstanding);
        }).toList();

        List<ExportService.CasualWorkLogRow> workLogRows = sessions.stream()
                .filter(s -> s.getSessionDate().getYear() == year && s.getSessionDate().getMonthValue() == month)
                .flatMap(s -> s.getEntries().stream()
                        .map(e -> new ExportService.CasualWorkLogRow(
                                e.getEmployee().getFullName(),
                                s.getSessionDate().getDayOfMonth(), year, month,
                                s.getActivity(), e.effectiveRate(), e.effectiveRate())))
                .sorted(java.util.Comparator.comparing(ExportService.CasualWorkLogRow::labourerName)
                        .thenComparingInt(ExportService.CasualWorkLogRow::day))
                .toList();

        List<ExportService.CasualPaymentRow> paymentRows = paymentRepository.findByFarmIdOrdered(farmId)
                .stream()
                .map(p -> new ExportService.CasualPaymentRow(
                        p.getEmployee().getFullName(), p.getPaymentDate(),
                        p.getAmount(), p.getNote(), p.getPaidBy()))
                .toList();

        return exportService.generateCasualMonthlyExcel(year, month, summaryRows, workLogRows, paymentRows);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private CasualLabourerDto toDto(Employee e) {
        String photoBase64 = e.getPhotoData() != null
                ? Base64.getEncoder().encodeToString(e.getPhotoData()) : null;
        return new CasualLabourerDto(
                e.getId(),
                e.getLsNumber(),
                e.getEmployeeId(),
                e.getFirstName(),
                e.getLastName(),
                e.getFullName(),
                e.getPhone(),
                photoBase64,
                e.getPhotoMimeType(),
                e.getJobTitle(),
                e.getDepartment() != null ? e.getDepartment().getName() : null
        );
    }

    private CasualWorkSessionDto toSessionDto(CasualWorkSession session) {
        List<CasualWorkEntryDto> entries = session.getEntries().stream()
                .map(e -> new CasualWorkEntryDto(
                        e.getId(),
                        e.getEmployee().getId(),
                        e.getEmployee().getFullName(),
                        e.getRateOverride(),
                        e.effectiveRate()))
                .toList();
        return new CasualWorkSessionDto(session.getId(), session.getSessionDate(),
                session.getActivity(), session.getDefaultDailyRate(), entries);
    }

    private CasualLabourerPaymentDto toPaymentDto(CasualLabourerPayment p) {
        return new CasualLabourerPaymentDto(
                p.getId(), p.getEmployee().getId(), p.getEmployee().getFullName(),
                p.getPaymentDate(), p.getAmount(), p.getNote(), p.getPaidBy(), p.getCreatedAt());
    }

    private Integer getReportIdForMonth(Integer farmId, Integer year, Integer month) {
        return monthlyReportRepository.findByFarmIdAndYearAndMonth(farmId, year, month)
                .map(MonthlyReport::getId)
                .orElse(null);
    }

    private static BigDecimal coalesce(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }

    private static String resolveFirstName(String firstName, String legacyName) {
        if (firstName != null && !firstName.isBlank()) return firstName.trim();
        if (legacyName != null && !legacyName.isBlank()) {
            String n = legacyName.trim();
            int sp = n.indexOf(' ');
            return sp >= 0 ? n.substring(0, sp) : n;
        }
        return null;
    }

    private static String resolveLastName(String firstName, String lastName, String legacyName) {
        if (firstName != null && !firstName.isBlank()) {
            return lastName != null && !lastName.isBlank() ? lastName.trim() : null;
        }
        if (legacyName != null && !legacyName.isBlank()) {
            String n = legacyName.trim();
            int sp = n.indexOf(' ');
            return sp >= 0 ? n.substring(sp + 1) : null;
        }
        return null;
    }
}
