package com.farmreports.api.service;

import com.farmreports.api.dto.*;
import com.farmreports.api.entity.CasualLabourer;
import com.farmreports.api.entity.CasualLabourerPayment;
import com.farmreports.api.entity.Farm;
import com.farmreports.api.repository.CasualAttendanceRepository;
import com.farmreports.api.repository.CasualLabourerPaymentRepository;
import com.farmreports.api.repository.CasualLabourerRepository;
import com.farmreports.api.repository.FarmRepository;
import com.farmreports.api.repository.MonthlyReportRepository;
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
        labourer.setDefaultDailyRate(request.defaultDailyRate());

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

    // ── Summary ───────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public CasualLabourerSummaryDto getSummary(Integer farmId, Integer labourerId) {
        casualLabourerRepository.findByIdAndFarmId(labourerId, farmId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Casual labourer not found"));

        BigDecimal allTimeEarned = casualAttendanceRepository.sumAllTimeEarnedByLabourerId(labourerId);
        BigDecimal allTimePaid   = paymentRepository.sumAmountByLabourerId(labourerId);
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

    // ── Export ────────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public byte[] generateMonthlyExcel(Integer farmId, Integer year, Integer month) {
        List<CasualLabourer> labourers = casualLabourerRepository.findByFarmIdAndActiveTrue(farmId);

        // Load this month's attendance once
        Integer reportId = getReportIdForMonth(farmId, year, month);
        var monthAttendance = reportId != null
                ? casualAttendanceRepository.findByReportId(reportId)
                : List.<com.farmreports.api.entity.CasualAttendance>of();

        List<ExportService.CasualLabourerRow> summaryRows = labourers.stream().map(l -> {
            BigDecimal monthEarned = monthAttendance.stream()
                    .filter(ca -> ca.getCasualLabourer().getId().equals(l.getId()) && ca.isPresent())
                    .map(ca -> ca.getRateOverride() != null ? ca.getRateOverride() : l.getDefaultDailyRate())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal allTimePaid   = paymentRepository.sumAmountByLabourerId(l.getId());
            BigDecimal allTimeEarned = casualAttendanceRepository.sumAllTimeEarnedByLabourerId(l.getId());
            BigDecimal outstanding   = allTimeEarned.subtract(allTimePaid);
            return new ExportService.CasualLabourerRow(l.getName(), l.getPhone(), monthEarned, allTimePaid, outstanding);
        }).toList();

        List<ExportService.CasualWorkLogRow> workLogRows = monthAttendance.stream()
                .filter(ca -> ca.isPresent())
                .map(ca -> {
                    BigDecimal rate = ca.getRateOverride() != null
                            ? ca.getRateOverride()
                            : ca.getCasualLabourer().getDefaultDailyRate();
                    return new ExportService.CasualWorkLogRow(
                            ca.getCasualLabourer().getName(), ca.getDayOfMonth(), year, month,
                            ca.getTaskDescription(), rate, rate);
                })
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
        String photoBase64 = null;
        if (labourer.getPhotoData() != null) {
            photoBase64 = Base64.getEncoder().encodeToString(labourer.getPhotoData());
        }
        return new CasualLabourerDto(
                labourer.getId(),
                labourer.getName(),
                labourer.getPhone(),
                labourer.getDefaultDailyRate(),
                photoBase64,
                labourer.getPhotoMimeType()
        );
    }

    private CasualLabourerPaymentDto toPaymentDto(CasualLabourerPayment p) {
        return new CasualLabourerPaymentDto(
                p.getId(),
                p.getCasualLabourer().getId(),
                p.getCasualLabourer().getName(),
                p.getPaymentDate(),
                p.getAmount(),
                p.getNote(),
                p.getPaidBy(),
                p.getCreatedAt()
        );
    }
}
