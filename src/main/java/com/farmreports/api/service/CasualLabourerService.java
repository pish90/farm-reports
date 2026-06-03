package com.farmreports.api.service;

import com.farmreports.api.dto.*;
import com.farmreports.api.entity.*;
import com.farmreports.api.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CasualLabourerService {

    private final CasualLabourerRepository casualLabourerRepository;
    private final CasualLabourerPaymentRepository paymentRepository;
    private final CasualAttendanceRepository casualAttendanceRepository;
    private final CasualWorkSessionRepository workSessionRepository;
    private final CasualWorkEntryRepository workEntryRepository;
    private final FarmRepository farmRepository;
    private final MonthlyReportRepository monthlyReportRepository;
    private final ExportService exportService;

    // ── Labourers ────────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<CasualLabourerDto> getActiveCasualLabourers(Integer farmId) {
        return casualLabourerRepository.findByFarmIdAndActiveTrue(farmId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public CasualLabourerDto addCasualLabourer(Integer farmId, CasualLabourerRequest request) {
        Farm farm = farmRepository.findById(farmId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Farm not found"));

        CasualLabourer labourer = new CasualLabourer();
        labourer.setFarm(farm);
        labourer.setName(request.name().trim());
        labourer.setPhone(request.phone() != null ? request.phone().trim() : null);

        if (request.photoBase64() != null && !request.photoBase64().isBlank()) {
            labourer.setPhotoData(Base64.getDecoder().decode(request.photoBase64()));
            labourer.setPhotoMimeType(request.photoMimeType() != null ? request.photoMimeType() : "image/jpeg");
        }

        return toDto(casualLabourerRepository.save(labourer));
    }

    @Transactional
    public void deactivateCasualLabourer(Integer farmId, Integer labourerId) {
        CasualLabourer labourer = casualLabourerRepository.findByIdAndFarmId(labourerId, farmId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Casual labourer not found"));
        labourer.setActive(false);
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
            CasualLabourer labourer = casualLabourerRepository.findByIdAndFarmId(er.casualLabourerId(), farmId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Casual labourer not found: " + er.casualLabourerId()));
            CasualWorkEntry entry = new CasualWorkEntry();
            entry.setSession(session);
            entry.setCasualLabourer(labourer);
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
            CasualLabourer labourer = casualLabourerRepository.findByIdAndFarmId(er.casualLabourerId(), farmId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Casual labourer not found: " + er.casualLabourerId()));
            CasualWorkEntry entry = new CasualWorkEntry();
            entry.setSession(session);
            entry.setCasualLabourer(labourer);
            entry.setRateOverride(er.rateOverride());
            session.getEntries().add(entry);
        }

        return toSessionDto(workSessionRepository.save(session));
    }

    @Transactional(readOnly = true)
    public List<CasualWorkSessionDto> getWorkSessions(Integer farmId) {
        return workSessionRepository.findByFarmIdWithEntries(farmId)
                .stream()
                .map(this::toSessionDto)
                .toList();
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
        List<CasualLabourer> labourers = casualLabourerRepository.findByFarmIdAndActiveTrue(farmId);
        List<CasualWorkSession> sessions = workSessionRepository.findByFarmIdWithEntries(farmId);

        return labourers.stream().map(l -> {
            BigDecimal earned = workEntryRepository.sumEarnedByLabourerId(l.getId());
            BigDecimal paid   = coalesce(paymentRepository.sumAmountByLabourerId(l.getId()));
            BigDecimal balance = coalesce(earned).subtract(paid);

            List<CasualLabourerReportDto.WorkEntryLine> lines = sessions.stream()
                    .flatMap(s -> s.getEntries().stream()
                            .filter(e -> e.getCasualLabourer().getId().equals(l.getId()))
                            .map(e -> new CasualLabourerReportDto.WorkEntryLine(
                                    s.getId(), s.getSessionDate(), s.getActivity(), e.effectiveRate())))
                    .toList();

            String photoBase64 = l.getPhotoData() != null
                    ? Base64.getEncoder().encodeToString(l.getPhotoData()) : null;

            return new CasualLabourerReportDto(
                    l.getId(), l.getName(), l.getPhone(),
                    photoBase64, l.getPhotoMimeType(),
                    coalesce(earned), paid, balance, lines);
        }).toList();
    }

    // ── Summary (per-labourer) ────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public CasualLabourerSummaryDto getSummary(Integer farmId, Integer labourerId) {
        casualLabourerRepository.findByIdAndFarmId(labourerId, farmId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Casual labourer not found"));

        BigDecimal allTimeEarned = coalesce(workEntryRepository.sumEarnedByLabourerId(labourerId));
        BigDecimal allTimePaid   = coalesce(paymentRepository.sumAmountByLabourerId(labourerId));
        BigDecimal outstanding   = allTimeEarned.subtract(allTimePaid);

        List<CasualLabourerPaymentDto> payments = paymentRepository
                .findByCasualLabourerIdOrderByPaymentDateDesc(labourerId)
                .stream()
                .map(this::toPaymentDto)
                .toList();

        return new CasualLabourerSummaryDto(allTimeEarned, allTimePaid, outstanding, payments);
    }

    // ── Payments ──────────────────────────────────────────────────────────────

    @Transactional
    public CasualLabourerPaymentDto recordPayment(Integer farmId, Integer labourerId,
                                                   RecordPaymentRequest request, String paidBy) {
        CasualLabourer labourer = casualLabourerRepository.findByIdAndFarmId(labourerId, farmId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Casual labourer not found"));
        Farm farm = farmRepository.getReferenceById(farmId);

        CasualLabourerPayment payment = new CasualLabourerPayment();
        payment.setCasualLabourer(labourer);
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
        if (!payment.getCasualLabourer().getId().equals(labourerId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found");
        }
        paymentRepository.delete(payment);
    }

    // ── Monthly payroll (legacy — historical casual_attendance data only) ─────

    @Transactional(readOnly = true)
    public List<CasualPayrollEntryDto> getMonthlyPayroll(Integer farmId, Integer year, Integer month) {
        List<CasualLabourer> labourers = casualLabourerRepository.findByFarmIdAndActiveTrue(farmId);

        Integer reportId = getReportIdForMonth(farmId, year, month);
        var monthAttendance = reportId != null
                ? casualAttendanceRepository.findByReportId(reportId)
                : List.<CasualAttendance>of();

        return labourers.stream().map(l -> {
            var mine = monthAttendance.stream()
                    .filter(ca -> ca.getCasualLabourer().getId().equals(l.getId()) && ca.isPresent())
                    .toList();

            BigDecimal monthEarnings = mine.stream()
                    .map(ca -> ca.getRateOverride() != null ? ca.getRateOverride() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal allTimePaid   = coalesce(paymentRepository.sumAmountByLabourerId(l.getId()));
            BigDecimal allTimeEarned = coalesce(workEntryRepository.sumEarnedByLabourerId(l.getId()));
            BigDecimal outstanding   = allTimeEarned.subtract(allTimePaid);

            String photoBase64 = l.getPhotoData() != null
                    ? Base64.getEncoder().encodeToString(l.getPhotoData()) : null;

            return new CasualPayrollEntryDto(
                    l.getId(), l.getName(), l.getPhone(),
                    photoBase64, l.getPhotoMimeType(),
                    mine.size(), monthEarnings, allTimePaid, outstanding);

        }).toList();
    }

    // ── Export ────────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public byte[] generateMonthlyExcel(Integer farmId, Integer year, Integer month) {
        List<CasualLabourer> labourers = casualLabourerRepository.findByFarmIdAndActiveTrue(farmId);
        List<CasualWorkSession> sessions = workSessionRepository.findByFarmIdWithEntries(farmId);

        List<ExportService.CasualLabourerRow> summaryRows = labourers.stream().map(l -> {
            BigDecimal allTimeEarned = coalesce(workEntryRepository.sumEarnedByLabourerId(l.getId()));
            BigDecimal allTimePaid   = coalesce(paymentRepository.sumAmountByLabourerId(l.getId()));
            BigDecimal outstanding   = allTimeEarned.subtract(allTimePaid);

            BigDecimal monthEarned = sessions.stream()
                    .filter(s -> s.getSessionDate().getYear() == year && s.getSessionDate().getMonthValue() == month)
                    .flatMap(s -> s.getEntries().stream()
                            .filter(e -> e.getCasualLabourer().getId().equals(l.getId())))
                    .map(CasualWorkEntry::effectiveRate)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            return new ExportService.CasualLabourerRow(l.getName(), l.getPhone(), monthEarned, allTimePaid, outstanding);
        }).toList();

        List<ExportService.CasualWorkLogRow> workLogRows = sessions.stream()
                .filter(s -> s.getSessionDate().getYear() == year && s.getSessionDate().getMonthValue() == month)
                .flatMap(s -> s.getEntries().stream()
                        .map(e -> new ExportService.CasualWorkLogRow(
                                e.getCasualLabourer().getName(),
                                s.getSessionDate().getDayOfMonth(), year, month,
                                s.getActivity(), e.effectiveRate(), e.effectiveRate())))
                .sorted(java.util.Comparator.comparing(ExportService.CasualWorkLogRow::labourerName)
                        .thenComparingInt(ExportService.CasualWorkLogRow::day))
                .toList();

        List<ExportService.CasualPaymentRow> paymentRows = paymentRepository.findByFarmIdOrdered(farmId)
                .stream()
                .map(p -> new ExportService.CasualPaymentRow(
                        p.getCasualLabourer().getName(), p.getPaymentDate(),
                        p.getAmount(), p.getNote(), p.getPaidBy()))
                .toList();

        return exportService.generateCasualMonthlyExcel(year, month, summaryRows, workLogRows, paymentRows);
    }

    private Integer getReportIdForMonth(Integer farmId, Integer year, Integer month) {
        return monthlyReportRepository.findByFarmIdAndYearAndMonth(farmId, year, month)
                .map(r -> r.getId())
                .orElse(null);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private CasualLabourerDto toDto(CasualLabourer labourer) {
        String photoBase64 = labourer.getPhotoData() != null
                ? Base64.getEncoder().encodeToString(labourer.getPhotoData()) : null;
        return new CasualLabourerDto(labourer.getId(), labourer.getName(), labourer.getPhone(),
                photoBase64, labourer.getPhotoMimeType());
    }

    private CasualWorkSessionDto toSessionDto(CasualWorkSession session) {
        List<CasualWorkEntryDto> entries = session.getEntries().stream()
                .map(e -> new CasualWorkEntryDto(
                        e.getId(),
                        e.getCasualLabourer().getId(),
                        e.getCasualLabourer().getName(),
                        e.getRateOverride(),
                        e.effectiveRate()))
                .toList();
        return new CasualWorkSessionDto(session.getId(), session.getSessionDate(),
                session.getActivity(), session.getDefaultDailyRate(), entries);
    }

    private CasualLabourerPaymentDto toPaymentDto(CasualLabourerPayment p) {
        return new CasualLabourerPaymentDto(
                p.getId(), p.getCasualLabourer().getId(), p.getCasualLabourer().getName(),
                p.getPaymentDate(), p.getAmount(), p.getNote(), p.getPaidBy(), p.getCreatedAt());
    }

    private BigDecimal coalesce(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }
}
