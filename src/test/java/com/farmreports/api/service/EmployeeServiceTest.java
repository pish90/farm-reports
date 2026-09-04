package com.farmreports.api.service;

import com.farmreports.api.dto.EmployeeCsvImportResult;
import com.farmreports.api.dto.EmployeeDto;
import com.farmreports.api.dto.EmployeeRequest;
import com.farmreports.api.dto.PageDto;
import com.farmreports.api.entity.Employee;
import com.farmreports.api.entity.EmploymentType;
import com.farmreports.api.entity.Farm;
import com.farmreports.api.repository.DepartmentRepository;
import com.farmreports.api.repository.EmployeePaymentRepository;
import com.farmreports.api.repository.EmployeeRepository;
import com.farmreports.api.repository.FarmRepository;
import com.farmreports.api.repository.PayrollEntryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock EmployeeRepository employeeRepo;
    @Mock EmployeePaymentRepository paymentRepo;
    @Mock PayrollEntryRepository payrollRepo;
    @Mock DepartmentRepository departmentRepo;
    @Mock FarmRepository farmRepo;
    @Mock EmployeeIdService employeeIdService;
    @Mock JdbcTemplate jdbc;

    EmployeeService employeeService;

    Farm matunda;
    Farm lesA;
    Farm kenlet;

    @BeforeEach
    void setUp() {
        employeeService = new EmployeeService(
                employeeRepo, paymentRepo, payrollRepo, departmentRepo, farmRepo, employeeIdService, jdbc);

        matunda = new Farm();
        matunda.setId(1);
        matunda.setName("Matunda");

        lesA = new Farm();
        lesA.setId(2);
        lesA.setName("Les A");

        kenlet = new Farm();
        kenlet.setId(3);
        kenlet.setName("Kenlet");
    }

    @Test
    void importEmployeesFromCsv_allValidRows_insertsAllAndReportsSuccess() {
        when(farmRepo.findAll()).thenReturn(List.of(matunda, lesA, kenlet));
        when(farmRepo.findById(1)).thenReturn(Optional.of(matunda));
        when(farmRepo.findById(2)).thenReturn(Optional.of(lesA));
        when(employeeIdService.generateFor(anyString())).thenReturn("MAT0001");
        when(employeeIdService.generateLsNumber(anyString())).thenReturn("LS2001M");
        when(employeeRepo.save(any(Employee.class))).thenAnswer(inv -> {
            Employee e = inv.getArgument(0);
            e.setId(1);
            return e;
        });

        String csv = "farmName,firstName,lastName,phone,employmentType,jobTitle,startDate,defaultDailyRate\n"
                + "Matunda,Jane,Doe,0712345678,SALARIED,Herdsman,2024-01-15,\n"
                + "les a,John,Smith,,CASUAL,General worker,,450.00\n";
        MockMultipartFile file = new MockMultipartFile(
                "file", "employees.csv", "text/csv", csv.getBytes(StandardCharsets.UTF_8));

        EmployeeCsvImportResult result = employeeService.importEmployeesFromCsv(file);

        assertThat(result.success()).isTrue();
        assertThat(result.totalRows()).isEqualTo(2);
        assertThat(result.importedCount()).isEqualTo(2);
        assertThat(result.errors()).isEmpty();
        verify(employeeIdService, times(2)).generateFor(anyString());
        verify(employeeIdService, times(2)).generateLsNumber(anyString());
        verify(employeeRepo, times(2)).save(any(Employee.class));
    }

    @Test
    void importEmployeesFromCsv_badRow_insertsNothingAndReportsError() {
        when(farmRepo.findAll()).thenReturn(List.of(matunda, lesA, kenlet));

        String csv = "farmName,firstName,lastName,phone,employmentType,jobTitle,startDate,defaultDailyRate\n"
                + "Matunda,Jane,Doe,0712345678,SALARIED,Herdsman,2024-01-15,\n"
                + "Les A,John,Smith,,CASUAL,General worker,,450.00\n"
                + "Kenlet,Mary,,0700111222,PARTTIME,,,\n";
        MockMultipartFile file = new MockMultipartFile(
                "file", "employees.csv", "text/csv", csv.getBytes(StandardCharsets.UTF_8));

        EmployeeCsvImportResult result = employeeService.importEmployeesFromCsv(file);

        assertThat(result.success()).isFalse();
        assertThat(result.totalRows()).isEqualTo(3);
        assertThat(result.importedCount()).isEqualTo(0);
        assertThat(result.errors()).hasSize(1);
        assertThat(result.errors().get(0).row()).isEqualTo(4);
        assertThat(result.errors().get(0).message()).contains("PARTTIME");

        // Validation-failure must write nothing: no LS numbers burned, no rows saved.
        verifyNoInteractions(employeeIdService);
        verify(employeeRepo, never()).save(any());
    }

    @Test
    void importEmployeesFromCsv_matchesExistingEmployee_rejectsRowAsDuplicate() {
        when(farmRepo.findAll()).thenReturn(List.of(matunda, lesA, kenlet));

        Employee existing = new Employee();
        existing.setFarm(matunda);
        existing.setFirstName("Jane");
        existing.setLastName("Doe");
        when(employeeRepo.findAll()).thenReturn(List.of(existing));

        String csv = "farmName,firstName,lastName,phone,employmentType,jobTitle,startDate,defaultDailyRate\n"
                + "Matunda,jane,doe,0712345678,SALARIED,Herdsman,2024-01-15,\n";
        MockMultipartFile file = new MockMultipartFile(
                "file", "employees.csv", "text/csv", csv.getBytes(StandardCharsets.UTF_8));

        EmployeeCsvImportResult result = employeeService.importEmployeesFromCsv(file);

        assertThat(result.success()).isFalse();
        assertThat(result.importedCount()).isEqualTo(0);
        assertThat(result.errors()).hasSize(1);
        assertThat(result.errors().get(0).message()).contains("already exists");
        verifyNoInteractions(employeeIdService);
        verify(employeeRepo, never()).save(any());
    }

    @Test
    void importEmployeesFromCsv_duplicateRowsInSameFile_rejectsSecondOccurrence() {
        when(farmRepo.findAll()).thenReturn(List.of(matunda, lesA, kenlet));
        when(employeeRepo.findAll()).thenReturn(List.of());

        String csv = "farmName,firstName,lastName,phone,employmentType,jobTitle,startDate,defaultDailyRate\n"
                + "Matunda,Jane,Doe,0712345678,SALARIED,Herdsman,2024-01-15,\n"
                + "matunda,JANE,DOE,,CASUAL,General worker,,450.00\n";
        MockMultipartFile file = new MockMultipartFile(
                "file", "employees.csv", "text/csv", csv.getBytes(StandardCharsets.UTF_8));

        EmployeeCsvImportResult result = employeeService.importEmployeesFromCsv(file);

        assertThat(result.success()).isFalse();
        assertThat(result.importedCount()).isEqualTo(0);
        assertThat(result.errors()).hasSize(1);
        assertThat(result.errors().get(0).row()).isEqualTo(3);
        assertThat(result.errors().get(0).message()).contains("Duplicate row in file");
        verifyNoInteractions(employeeIdService);
        verify(employeeRepo, never()).save(any());
    }

    @Test
    void createEmployee_matchesExistingEmployee_throwsConflict() {
        when(farmRepo.findById(1)).thenReturn(Optional.of(matunda));
        when(employeeRepo.existsDuplicate(eq(1), eq("Jane"), eq("Doe"), isNull())).thenReturn(true);

        EmployeeRequest request = new EmployeeRequest(
                "Jane", "Doe", null, "SALARIED", null, null, null, null, null, null, null, null, null, null);

        assertThatThrownBy(() -> employeeService.createEmployee(1, request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("already exists");

        verifyNoInteractions(employeeIdService);
        verify(employeeRepo, never()).save(any());
    }

    @Test
    void updateEmployee_renamedToMatchAnotherEmployee_throwsConflict() {
        Employee existing = new Employee();
        existing.setId(5);
        existing.setFarm(matunda);
        existing.setFirstName("Old");
        existing.setLastName("Name");
        when(employeeRepo.findByIdAndFarmId(5, 1)).thenReturn(Optional.of(existing));
        when(employeeRepo.existsDuplicate(1, "Jane", "Doe", 5)).thenReturn(true);

        EmployeeRequest request = new EmployeeRequest(
                "Jane", "Doe", null, "SALARIED", null, null, null, null, null, null, null, null, null, null);

        assertThatThrownBy(() -> employeeService.updateEmployee(1, 5, request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("already exists");

        verify(employeeRepo, never()).save(any());
    }

    @Test
    @SuppressWarnings("unchecked")
    void getEmployees_defaultsToPageZeroSizeTen() {
        Employee jane = new Employee();
        jane.setId(1);
        jane.setFarm(matunda);
        jane.setFirstName("Jane");
        jane.setLastName("Doe");
        jane.setEmploymentType(EmploymentType.SALARIED);
        jane.setStatus("ACTIVE");

        Page<Employee> page = new PageImpl<>(List.of(jane), Pageable.ofSize(10), 1);
        when(employeeRepo.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        PageDto<EmployeeDto> result = employeeService.getEmployees(1, null, null, null, 0, 10);

        assertThat(result.content()).hasSize(1);
        assertThat(result.content().get(0).fullName()).isEqualTo("Jane Doe");
        assertThat(result.totalElements()).isEqualTo(1);
        assertThat(result.page()).isEqualTo(0);
        assertThat(result.size()).isEqualTo(10);
    }

    @Test
    @SuppressWarnings("unchecked")
    void getAllEmployeesAcrossFarms_returnsPagedResultAcrossFarms() {
        Employee jane = new Employee();
        jane.setId(1);
        jane.setFarm(matunda);
        jane.setFirstName("Jane");
        jane.setLastName("Doe");
        jane.setEmploymentType(EmploymentType.SALARIED);
        jane.setStatus("ACTIVE");

        Page<Employee> page = new PageImpl<>(List.of(jane), Pageable.ofSize(10), 42);
        when(employeeRepo.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        PageDto<EmployeeDto> result = employeeService.getAllEmployeesAcrossFarms(null, null, null, 0, 10);

        assertThat(result.content()).hasSize(1);
        assertThat(result.totalElements()).isEqualTo(42);
    }

    @Test
    void deactivateEmployee_setsStatusInactive() {
        Employee jane = new Employee();
        jane.setId(1);
        jane.setFarm(matunda);
        jane.setFirstName("Jane");
        jane.setLastName("Doe");
        jane.setEmploymentType(EmploymentType.SALARIED);
        jane.setStatus("ACTIVE");
        when(employeeRepo.findByIdAndFarmId(1, 1)).thenReturn(Optional.of(jane));
        when(employeeRepo.save(any(Employee.class))).thenAnswer(inv -> inv.getArgument(0));

        EmployeeDto result = employeeService.deactivateEmployee(1, 1);

        assertThat(result.status()).isEqualTo("INACTIVE");
    }

    @Test
    void deleteEmployee_withHistory_throwsConflictAndDoesNotDelete() {
        Employee jane = new Employee();
        jane.setId(1);
        jane.setFarm(matunda);
        jane.setFirstName("Jane");
        jane.setLastName("Doe");
        when(employeeRepo.findByIdAndFarmId(1, 1)).thenReturn(Optional.of(jane));
        when(jdbc.queryForObject(anyString(), eq(Boolean.class), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(true);

        assertThatThrownBy(() -> employeeService.deleteEmployee(1, 1))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("deactivate");

        verify(employeeRepo, never()).delete(any(Employee.class));
    }

    @Test
    void deleteEmployee_noHistory_deletes() {
        Employee jane = new Employee();
        jane.setId(1);
        jane.setFarm(matunda);
        jane.setFirstName("Jane");
        jane.setLastName("Doe");
        when(employeeRepo.findByIdAndFarmId(1, 1)).thenReturn(Optional.of(jane));
        when(jdbc.queryForObject(anyString(), eq(Boolean.class), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(false);

        employeeService.deleteEmployee(1, 1);

        verify(employeeRepo).delete(jane);
    }
}
