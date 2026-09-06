package com.farmreports.api.service;

import com.farmreports.api.dto.CasualLabourerDto;
import com.farmreports.api.entity.Employee;
import com.farmreports.api.entity.Farm;
import com.farmreports.api.repository.CasualAttendanceRepository;
import com.farmreports.api.repository.CasualLabourerPaymentRepository;
import com.farmreports.api.repository.CasualWorkEntryRepository;
import com.farmreports.api.repository.CasualWorkSessionRepository;
import com.farmreports.api.repository.DepartmentRepository;
import com.farmreports.api.repository.EmployeeRepository;
import com.farmreports.api.repository.FarmRepository;
import com.farmreports.api.repository.MonthlyReportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CasualLabourerServiceTest {

    @Mock EmployeeRepository employeeRepository;
    @Mock DepartmentRepository departmentRepository;
    @Mock CasualLabourerPaymentRepository paymentRepository;
    @Mock CasualWorkSessionRepository workSessionRepository;
    @Mock CasualWorkEntryRepository workEntryRepository;
    @Mock FarmRepository farmRepository;
    @Mock MonthlyReportRepository monthlyReportRepository;
    @Mock CasualAttendanceRepository casualAttendanceRepository;
    @Mock EmployeeIdService employeeIdService;
    @Mock ExportService exportService;

    CasualLabourerService service;

    Farm matunda;

    @BeforeEach
    void setUp() {
        service = new CasualLabourerService(employeeRepository, departmentRepository, paymentRepository,
                workSessionRepository, workEntryRepository, farmRepository, monthlyReportRepository,
                casualAttendanceRepository, employeeIdService, exportService);

        matunda = new Farm();
        matunda.setId(1);
        matunda.setName("Matunda");
    }

    private static Employee employee(int id, String firstName, boolean salaried, boolean casual) {
        Employee e = new Employee();
        e.setId(id);
        e.setFirstName(firstName);
        e.setSalaried(salaried);
        e.setCasual(casual);
        e.setStatus("ACTIVE");
        return e;
    }

    @Test
    void getActiveCasualLabourers_includesSalariedOnlyEmployees_notJustCasualFlagged() {
        Employee salariedOnly = employee(1, "Jane", true, false);
        Employee casualOnly = employee(2, "John", false, true);
        Employee both = employee(3, "Mary", true, true);

        when(employeeRepository.findByFarmIdAndStatus(1, "ACTIVE"))
                .thenReturn(List.of(salariedOnly, casualOnly, both));

        List<CasualLabourerDto> result = service.getActiveCasualLabourers(1);

        assertThat(result).hasSize(3);
        assertThat(result).extracting(CasualLabourerDto::firstName)
                .containsExactlyInAnyOrder("Jane", "John", "Mary");
        CasualLabourerDto janeDto = result.stream().filter(d -> d.firstName().equals("Jane")).findFirst().orElseThrow();
        assertThat(janeDto.isSalaried()).isTrue();
        assertThat(janeDto.isCasual()).isFalse();
    }

    @Test
    void deactivateCasualLabourer_worksForSalariedOnlyEmployee() {
        Employee salariedOnly = employee(5, "Peter", true, false);
        when(employeeRepository.findByIdAndFarmId(5, 1)).thenReturn(java.util.Optional.of(salariedOnly));

        service.deactivateCasualLabourer(1, 5);

        assertThat(salariedOnly.getStatus()).isEqualTo("INACTIVE");
    }

    @Test
    void deactivateCasualLabourer_notFound_throws404() {
        when(employeeRepository.findByIdAndFarmId(99, 1)).thenReturn(java.util.Optional.empty());

        assertThatThrownBy(() -> service.deactivateCasualLabourer(1, 99))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Employee not found");
    }
}
