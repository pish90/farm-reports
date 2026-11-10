package com.farmreports.api.service;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CasualSheetImportServiceTest {

    @Mock FarmRepository farmRepo;
    @Mock EmployeeRepository employeeRepo;
    @Mock CasualWorkSessionRepository sessionRepo;
    @Mock CasualLabourerPaymentRepository paymentRepo;
    @Mock EmployeeIdService employeeIdService;
    @Mock EntityManager em;
    @InjectMocks CasualSheetImportService service;

    private final Farm farm = new Farm();
    private final LocalDate saturday = LocalDate.of(2026, 6, 13);

    private Employee emp(int id, String first, String last) {
        Employee e = new Employee();
        e.setId(id);
        e.setFirstName(first);
        e.setLastName(last);
        e.setFarm(farm);
        e.setCasual(true);
        return e;
    }

    @BeforeEach
    void setUp() {
        farm.setId(3);
        farm.setName("Les B");
        when(farmRepo.findById(3)).thenReturn(Optional.of(farm));
        Employee ronald = emp(1, "Ronald", "Misoi");
        Employee joan = emp(4, "Joan", "Cherono");
        when(employeeRepo.findByIdAndFarmId(1, 3)).thenReturn(Optional.of(ronald));
        when(employeeRepo.findByIdAndFarmId(4, 3)).thenReturn(Optional.of(joan));
        when(employeeRepo.getReferenceById(1)).thenReturn(ronald);
        when(employeeRepo.getReferenceById(4)).thenReturn(joan);
        when(employeeRepo.findByFarmId(3)).thenReturn(List.of(ronald, joan));
        when(employeeIdService.generateFor("Les B")).thenReturn("LES0100");
        when(employeeIdService.generateLsNumber("Les B")).thenReturn("LS2300B");
        when(employeeRepo.save(any(Employee.class))).thenAnswer(inv -> {
            Employee e = inv.getArgument(0);
            e.setId(99);
            when(employeeRepo.getReferenceById(99)).thenReturn(e);
            return e;
        });
    }

    @Test
    void savesOneSessionPerSheetWithPaymentsOnlyForSignedRows() {
        SaveSheet sheet = new SaveSheet("Detasselling", "1st week June 2026 8th-13th", saturday, new BigDecimal("200"), List.of(
                new SaveRow(1, null, null, new BigDecimal("1200"), true),
                new SaveRow(4, null, null, new BigDecimal("880"), false),
                // Ronald listed again further down the same sheet -> summed into one entry
                new SaveRow(1, null, null, new BigDecimal("400"), true)), false);

        SaveResult r = service.save(new SaveRequest(3, List.of(sheet)), "Silas Khayundi");

        ArgumentCaptor<CasualWorkSession> s = ArgumentCaptor.forClass(CasualWorkSession.class);
        verify(sessionRepo).save(s.capture());
        CasualWorkSession session = s.getValue();
        assertThat(session.getSessionDate()).isEqualTo(saturday);
        assertThat(session.getActivity()).isEqualTo("Detasselling (1st week June 2026 8th-13th)");
        assertThat(session.getDefaultDailyRate()).isEqualByComparingTo("200");
        assertThat(session.getEntries()).extracting(e -> e.getEmployee().getId()).containsExactly(1, 4);
        assertThat(session.getEntries()).extracting(CasualWorkEntry::getRateOverride)
                .usingComparatorForType(BigDecimal::compareTo, BigDecimal.class)
                .containsExactly(new BigDecimal("1600"), new BigDecimal("880"));

        ArgumentCaptor<CasualLabourerPayment> p = ArgumentCaptor.forClass(CasualLabourerPayment.class);
        verify(paymentRepo, times(1)).save(p.capture());
        assertThat(p.getValue().getEmployee().getId()).isEqualTo(1);
        assertThat(p.getValue().getAmount()).isEqualByComparingTo("1600");
        assertThat(p.getValue().getPaymentDate()).isEqualTo(saturday);
        assertThat(p.getValue().getPaidBy()).isEqualTo("Silas Khayundi");
        assertThat(p.getValue().getNote()).startsWith(CasualSheetImportService.PAYMENT_NOTE_PREFIX);

        assertThat(r.entriesSaved()).isEqualTo(2);
        assertThat(r.paymentsRecorded()).isEqualTo(1);
        assertThat(r.totalEarned()).isEqualByComparingTo("2480");
        assertThat(r.totalPaid()).isEqualByComparingTo("1600");
    }

    @Test
    void newNameCreatesOneActiveCasualEvenIfListedTwice() {
        SaveSheet sheet = new SaveSheet("Collecting of manure", "", saturday, null, List.of(
                new SaveRow(null, "Lonah", "Chepleting", new BigDecimal("350"), true),
                new SaveRow(null, "lonah", "chepleting", new BigDecimal("350"), true)), false);

        SaveResult r = service.save(new SaveRequest(3, List.of(sheet)), "Silas");

        ArgumentCaptor<Employee> e = ArgumentCaptor.forClass(Employee.class);
        verify(employeeRepo, times(1)).save(e.capture());
        assertThat(e.getValue().isCasual()).isTrue();
        assertThat(e.getValue().isSalaried()).isFalse();
        assertThat(e.getValue().getStatus()).isEqualTo("ACTIVE");
        assertThat(e.getValue().getLsNumber()).isEqualTo("LS2300B");
        assertThat(r.employeesCreated()).isEqualTo(1);
        assertThat(r.entriesSaved()).isEqualTo(1);
    }

    @Test
    void newNameMatchingAnExistingEmployeeReusesThem() {
        SaveSheet sheet = new SaveSheet("Weeding", "", saturday, null, List.of(
                new SaveRow(null, "joan", "CHERONO", new BigDecimal("500"), false)), false);

        SaveResult r = service.save(new SaveRequest(3, List.of(sheet)), "Silas");

        verify(employeeRepo, never()).save(any());
        assertThat(r.employeesCreated()).isZero();
    }

    @Test
    void anInvalidRowAnywhereSavesNothing() {
        SaveSheet good = new SaveSheet("Weeding", "", saturday, null, List.of(new SaveRow(1, null, null, new BigDecimal("500"), true)), false);
        SaveSheet bad = new SaveSheet("Digging", "", saturday, null, List.of(new SaveRow(77, null, null, new BigDecimal("500"), true)), false);
        when(employeeRepo.findByIdAndFarmId(anyInt(), any())).thenAnswer(inv ->
                (Integer) inv.getArgument(0) == 1 ? Optional.of(emp(1, "Ronald", "Misoi")) : Optional.empty());

        assertThatThrownBy(() -> service.save(new SaveRequest(3, List.of(good, bad)), "Silas"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Sheet 2, row 1");
        verify(sessionRepo, never()).save(any());
        verify(paymentRepo, never()).save(any());
    }

    @Test
    void sheetWithoutDateOrWithZeroAmountIsRejected() {
        assertThatThrownBy(() -> service.save(new SaveRequest(3, List.of(
                new SaveSheet("Weeding", "", null, null, List.of(new SaveRow(1, null, null, BigDecimal.TEN, true)), false))), "S"))
                .hasMessageContaining("date");
        assertThatThrownBy(() -> service.save(new SaveRequest(3, List.of(
                new SaveSheet("Weeding", "", saturday, null, List.of(new SaveRow(1, null, null, BigDecimal.ZERO, true)), false))), "S"))
                .hasMessageContaining("more than 0");
    }

    @Test
    void sheetAlreadySavedIsRefusedUnlessConfirmed() {
        when(sessionRepo.existsByFarmIdAndSessionDateAndActivityStartingWithIgnoreCase(3, saturday, "Weeding")).thenReturn(true);
        SaveSheet repeat = new SaveSheet("Weeding", "", saturday, null, List.of(new SaveRow(1, null, null, new BigDecimal("500"), true)), false);

        assertThatThrownBy(() -> service.save(new SaveRequest(3, List.of(repeat)), "Silas"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("already saved");
        verify(sessionRepo, never()).save(any());

        SaveSheet confirmed = new SaveSheet("Weeding", "", saturday, null, List.of(new SaveRow(1, null, null, new BigDecimal("500"), true)), true);
        assertThat(service.save(new SaveRequest(3, List.of(confirmed)), "Silas").sheetsSaved()).isEqualTo(1);
    }

    @Test
    void sameSheetTwiceInOneUploadIsRefusedAndNothingIsSaved() {
        SaveSheet first = new SaveSheet("Weeding", "", saturday, null, List.of(new SaveRow(1, null, null, new BigDecimal("500"), true)), false);
        SaveSheet again = new SaveSheet(" weeding ", "", saturday, null, List.of(new SaveRow(4, null, null, new BigDecimal("500"), true)), false);

        assertThatThrownBy(() -> service.save(new SaveRequest(3, List.of(first, again)), "Silas"))
                .hasMessageContaining("Sheet 2")
                .hasMessageContaining("appears twice");
        verify(sessionRepo, never()).save(any());
        verify(paymentRepo, never()).save(any());
    }
}
