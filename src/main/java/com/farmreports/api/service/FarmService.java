package com.farmreports.api.service;

import com.farmreports.api.dto.*;
import com.farmreports.api.entity.*;
import com.farmreports.api.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FarmService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final LivestockTypeRepository livestockTypeRepository;
    private final FarmRepository farmRepository;
    private final EmployeeIdService employeeIdService;

    // ── Workers (SALARIED) ───────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<WorkerDto> getActiveWorkers(Integer farmId) {
        return employeeRepository
                .findByFarmIdAndStatusAndSalariedTrue(farmId, "ACTIVE")
                .stream()
                .map(this::toWorkerDto)
                .toList();
    }

    @Transactional
    public WorkerDto addWorker(Integer farmId, WorkerRequest request) {
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
        employee.setPhone(request.phone() != null ? request.phone().trim() : null);
        employee.setSalaried(true);
        employee.setCasual(false);
        employee.setJobTitle(request.jobTitle());
        employee.setStartDate(request.startDate() != null ? LocalDate.parse(request.startDate()) : null);
        if (request.departmentId() != null) {
            employee.setDepartment(departmentRepository.findByIdAndFarmId(request.departmentId(), farmId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Department not found")));
        }
        employee.setStatus("ACTIVE");

        return toWorkerDto(employeeRepository.save(employee));
    }

    @Transactional
    public WorkerDto updateWorker(Integer farmId, Integer workerId, WorkerRequest request) {
        Employee employee = employeeRepository.findByIdAndFarmIdAndSalariedTrue(workerId, farmId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Worker not found"));

        String firstName = resolveFirstName(request.firstName(), request.name());
        if (firstName != null) employee.setFirstName(firstName);
        if (request.lastName() != null) employee.setLastName(request.lastName().isBlank() ? null : request.lastName().trim());
        if (request.phone() != null) employee.setPhone(request.phone().isBlank() ? null : request.phone().trim());
        if (request.jobTitle() != null) employee.setJobTitle(request.jobTitle().isBlank() ? null : request.jobTitle().trim());
        if (request.startDate() != null) employee.setStartDate(LocalDate.parse(request.startDate()));
        if (request.departmentId() != null) {
            employee.setDepartment(departmentRepository.findByIdAndFarmId(request.departmentId(), farmId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Department not found")));
        }

        return toWorkerDto(employeeRepository.save(employee));
    }

    @Transactional
    public void deactivateWorker(Integer farmId, Integer workerId) {
        Employee employee = employeeRepository.findByIdAndFarmIdAndSalariedTrue(workerId, farmId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Worker not found"));
        employee.setStatus("INACTIVE");
    }

    // ── Departments ──────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<DepartmentDto> getDepartments(Integer farmId) {
        return departmentRepository.findByFarmIdOrderByName(farmId)
                .stream()
                .map(d -> new DepartmentDto(d.getId(), d.getName()))
                .toList();
    }

    @Transactional
    public DepartmentDto addDepartment(Integer farmId, DepartmentRequest request) {
        Farm farm = farmRepository.findById(farmId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Farm not found"));
        Department dept = new Department();
        dept.setFarm(farm);
        dept.setName(request.name().trim());
        return new DepartmentDto(departmentRepository.save(dept).getId(), dept.getName());
    }

    @Transactional
    public void deleteDepartment(Integer farmId, Integer deptId) {
        Department dept = departmentRepository.findByIdAndFarmId(deptId, farmId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Department not found"));
        departmentRepository.delete(dept);
    }

    // ── Livestock types ──────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public Map<String, List<LivestockTypeDto>> getLivestockTypesByCategory(Integer farmId) {
        return livestockTypeRepository.findByFarmId(farmId)
                .stream()
                .collect(Collectors.groupingBy(
                        lt -> lt.getCategory().name(),
                        Collectors.mapping(
                                lt -> new LivestockTypeDto(lt.getId(), lt.getCategory().name(), lt.getType()),
                                Collectors.toList()
                        )
                ));
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    WorkerDto toWorkerDto(Employee e) {
        return new WorkerDto(
                e.getId(),
                e.getLsNumber(),
                e.getEmployeeId(),
                e.getFirstName(),
                e.getLastName(),
                e.getFullName(),
                e.getPhone(),
                e.getJobTitle(),
                e.getDepartment() != null ? e.getDepartment().getName() : null,
                e.getStartDate() != null ? e.getStartDate().toString() : null,
                e.getStatus()
        );
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
