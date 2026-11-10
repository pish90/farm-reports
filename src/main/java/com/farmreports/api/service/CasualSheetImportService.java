package com.farmreports.api.service;

import com.farmreports.api.dto.CasualSheetDtos.CheckRequest;
import com.farmreports.api.dto.CasualSheetDtos.CheckResult;
import com.farmreports.api.dto.CasualSheetDtos.CheckSheet;
import com.farmreports.api.dto.CasualSheetDtos.CheckSheetResult;
import com.farmreports.api.dto.CasualSheetDtos.SaveRequest;
import com.farmreports.api.dto.CasualSheetDtos.SaveResult;
import com.farmreports.api.dto.CasualSheetDtos.SaveRow;
import com.farmreports.api.dto.CasualSheetDtos.SaveSheet;
import com.farmreports.api.entity.CasualLabourerPayment;
import com.farmreports.api.entity.CasualWorkEntry;
import com.farmreports.api.entity.CasualWorkSession;
import com.farmreports.api.entity.Employee;
import com.farmreports.api.entity.Farm;
import com.farmreports.api.repository.CasualLabourerPaymentRepository;
import com.farmreports.api.repository.CasualWorkSessionRepository;
import com.farmreports.api.repository.EmployeeRepository;
import com.farmreports.api.repository.FarmRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Saves reviewed casual pay sheets: one {@link CasualWorkSession} per sheet (dated the last day of
 * the sheet's week, activity = heading), one entry per person with {@code rateOverride} = the net
 * amount, plus a {@link CasualLabourerPayment} for every row marked paid. All-or-nothing.
 */
@Service
@RequiredArgsConstructor
public class CasualSheetImportService {

    private final FarmRepository farmRepo;
    private final EmployeeRepository employeeRepo;
    private final CasualWorkSessionRepository sessionRepo;
    private final CasualLabourerPaymentRepository paymentRepo;
    private final EmployeeIdService employeeIdService;
    private final EntityManager em;

    static final String PAYMENT_NOTE_PREFIX = "Casual sheet: ";

    /**
     * Flags, per sheet, people who already have pay for that month loaded from the monthly
     * spreadsheet import (so saving the sheet would count the work twice), and whether a session
     * with the same date + activity already exists (sheet saved before).
     */
    @Transactional(readOnly = true)
    public CheckResult check(CheckRequest request) {
        requireFarm(request.farmId());
        List<CheckSheetResult> out = new ArrayList<>();
        for (CheckSheet sheet : request.sheets() == null ? List.<CheckSheet>of() : request.sheets()) {
            if (sheet.date() == null) {
                out.add(new CheckSheetResult(sheet.key(), List.of(), false));
                continue;
            }
            List<Integer> ids = sheet.employeeIds() == null ? List.of()
                    : sheet.employeeIds().stream().filter(java.util.Objects::nonNull).distinct().toList();
            List<Integer> imported = ids.isEmpty() ? List.of()
                    : alreadyImported(request.farmId(), sheet.date(), ids);
            boolean duplicate = sheet.activity() != null && !sheet.activity().isBlank()
                    && existsSessionStartingWith(request.farmId(), sheet.date(), sheet.activity().trim());
            out.add(new CheckSheetResult(sheet.key(), imported, duplicate));
        }
        return new CheckResult(out);
    }

    private List<Integer> alreadyImported(Integer farmId, LocalDate date, List<Integer> employeeIds) {
        LocalDate monthEnd = date.withDayOfMonth(date.lengthOfMonth());
        LocalDate monthStart = date.withDayOfMonth(1);
        Set<Integer> hits = new HashSet<>(em.createQuery(
                        "SELECT e.employee.id FROM CasualWorkEntry e WHERE e.session.farm.id = :farmId " +
                        "AND e.session.sessionDate = :monthEnd AND e.session.activity = :tag " +
                        "AND e.employee.id IN :ids", Integer.class)
                .setParameter("farmId", farmId)
                .setParameter("monthEnd", monthEnd)
                .setParameter("tag", BulkImportService.PAY_IMPORT_TAG)
                .setParameter("ids", employeeIds)
                .getResultList());
        // Salaried/dual employees' spreadsheet pay went to payroll + a tagged employee_payments row.
        hits.addAll(em.createQuery(
                        "SELECT p.employeeId FROM EmployeePayment p WHERE p.employeeId IN :ids " +
                        "AND p.note = :tag AND p.paymentDate BETWEEN :start AND :end", Integer.class)
                .setParameter("ids", employeeIds)
                .setParameter("tag", BulkImportService.PAY_IMPORT_TAG)
                .setParameter("start", monthStart)
                .setParameter("end", monthEnd)
                .getResultList());
        return employeeIds.stream().filter(hits::contains).toList();
    }

    private boolean existsSessionStartingWith(Integer farmId, LocalDate date, String activity) {
        Long n = em.createQuery(
                        "SELECT COUNT(s) FROM CasualWorkSession s WHERE s.farm.id = :farmId " +
                        "AND s.sessionDate = :date AND LOWER(s.activity) LIKE :prefix", Long.class)
                .setParameter("farmId", farmId)
                .setParameter("date", date)
                .setParameter("prefix", activity.toLowerCase(Locale.ROOT) + "%")
                .getSingleResult();
        return n != null && n > 0;
    }

    @Transactional
    public SaveResult save(SaveRequest request, String paidByName) {
        Farm farm = requireFarm(request.farmId());
        if (request.sheets() == null || request.sheets().isEmpty()) {
            throw bad("Nothing to save");
        }

        // Validate everything first; write nothing if any sheet/row is invalid.
        Map<String, Employee> createdByName = new LinkedHashMap<>();
        for (int i = 0; i < request.sheets().size(); i++) {
            SaveSheet sheet = request.sheets().get(i);
            String where = "Sheet " + (i + 1);
            if (sheet.date() == null) throw bad(where + ": pick the date (last day of the week worked)");
            if (sheet.date().isAfter(LocalDate.now().plusDays(1))) throw bad(where + ": the date is in the future");
            if (sheet.activity() == null || sheet.activity().isBlank()) throw bad(where + ": enter the activity");
            if (sheet.rows() == null || sheet.rows().isEmpty()) throw bad(where + ": has no rows to save");
            for (int r = 0; r < sheet.rows().size(); r++) {
                SaveRow row = sheet.rows().get(r);
                String rw = where + ", row " + (r + 1);
                if (row.amount() == null || row.amount().signum() <= 0) throw bad(rw + ": amount must be more than 0");
                if (row.employeeId() != null) {
                    employeeRepo.findByIdAndFarmId(row.employeeId(), farm.getId())
                            .orElseThrow(() -> bad(rw + ": employee is not on " + farm.getName()));
                } else if (row.newFirstName() == null || row.newFirstName().isBlank()) {
                    throw bad(rw + ": choose the employee or enter a name for the new casual");
                }
            }
        }

        int entries = 0, payments = 0, sheetsSaved = 0;
        BigDecimal totalEarned = BigDecimal.ZERO, totalPaid = BigDecimal.ZERO;

        for (SaveSheet sheet : request.sheets()) {
            // Same person twice on one sheet (e.g. two activity lists on one page) -> one entry, amounts summed.
            Map<Integer, BigDecimal[]> byEmployee = new LinkedHashMap<>();
            for (SaveRow row : sheet.rows()) {
                Employee emp = row.employeeId() != null
                        ? employeeRepo.getReferenceById(row.employeeId())
                        : findOrCreateCasual(farm, row.newFirstName(), row.newLastName(), createdByName);
                BigDecimal[] sums = byEmployee.computeIfAbsent(emp.getId(), k -> new BigDecimal[]{BigDecimal.ZERO, BigDecimal.ZERO});
                sums[0] = sums[0].add(row.amount());
                if (row.paid()) sums[1] = sums[1].add(row.amount());
            }

            CasualWorkSession session = new CasualWorkSession();
            session.setFarm(farm);
            session.setSessionDate(sheet.date());
            session.setActivity(sessionActivity(sheet.activity(), sheet.periodText()));
            session.setDefaultDailyRate(sheet.ratePerDay() != null && sheet.ratePerDay().signum() > 0
                    ? sheet.ratePerDay() : BigDecimal.ZERO);
            for (Map.Entry<Integer, BigDecimal[]> e : byEmployee.entrySet()) {
                CasualWorkEntry entry = new CasualWorkEntry();
                entry.setSession(session);
                entry.setEmployee(employeeRepo.getReferenceById(e.getKey()));
                entry.setRateOverride(e.getValue()[0]);
                session.getEntries().add(entry);
                entries++;
                totalEarned = totalEarned.add(e.getValue()[0]);
            }
            sessionRepo.save(session);
            sheetsSaved++;

            String note = truncate(PAYMENT_NOTE_PREFIX + session.getActivity(), 500);
            for (Map.Entry<Integer, BigDecimal[]> e : byEmployee.entrySet()) {
                BigDecimal paid = e.getValue()[1];
                if (paid.signum() <= 0) continue;
                CasualLabourerPayment p = new CasualLabourerPayment();
                p.setEmployee(employeeRepo.getReferenceById(e.getKey()));
                p.setFarm(farm);
                p.setPaymentDate(sheet.date());
                p.setAmount(paid);
                p.setNote(note);
                p.setPaidBy(paidByName);
                paymentRepo.save(p);
                payments++;
                totalPaid = totalPaid.add(paid);
            }
        }
        return new SaveResult(sheetsSaved, entries, payments, createdByName.size(), totalEarned, totalPaid);
    }

    /** Reuses an existing employee with exactly this name on the farm, otherwise adds a new active casual. */
    private Employee findOrCreateCasual(Farm farm, String first, String last, Map<String, Employee> created) {
        String f = first.trim();
        String l = last == null || last.isBlank() ? null : last.trim();
        String key = (f + "|" + (l == null ? "" : l)).toLowerCase(Locale.ROOT);
        Employee already = created.get(key);
        if (already != null) return already;

        Employee existing = employeeRepo.findByFarmId(farm.getId()).stream()
                .filter(e -> e.getFirstName().trim().equalsIgnoreCase(f)
                        && (e.getLastName() == null ? "" : e.getLastName().trim()).equalsIgnoreCase(l == null ? "" : l))
                .findFirst().orElse(null);
        if (existing != null) {
            if (!existing.isCasual()) existing.setCasual(true);
            return existing;
        }

        Employee emp = new Employee();
        emp.setFarm(farm);
        emp.setEmployeeId(employeeIdService.generateFor(farm.getName()));
        emp.setLsNumber(employeeIdService.generateLsNumber(farm.getName()));
        emp.setFirstName(f);
        emp.setLastName(l);
        emp.setCasual(true);
        emp.setSalaried(false);
        emp.setStatus("ACTIVE");
        Employee saved = employeeRepo.save(emp);
        created.put(key, saved);
        return saved;
    }

    static String sessionActivity(String activity, String periodText) {
        String a = activity == null ? "" : activity.trim();
        String p = periodText == null ? "" : periodText.trim();
        return truncate(p.isEmpty() ? a : a + " (" + p + ")", 255);
    }

    private Farm requireFarm(Integer farmId) {
        if (farmId == null) throw bad("Choose a farm");
        return farmRepo.findById(farmId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Farm not found"));
    }

    private static String truncate(String s, int max) {
        return s.length() <= max ? s : s.substring(0, max);
    }

    private static ResponseStatusException bad(String msg) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, msg);
    }
}
