package com.farmreports.api.repository;

import com.farmreports.api.entity.CasualLabourerPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface CasualLabourerPaymentRepository extends JpaRepository<CasualLabourerPayment, Integer> {

    @Query("SELECT p FROM CasualLabourerPayment p WHERE p.employee.id = :employeeId ORDER BY p.paymentDate DESC")
    List<CasualLabourerPayment> findByEmployeeIdOrderByPaymentDateDesc(@Param("employeeId") Integer employeeId);

    @Query("SELECT p FROM CasualLabourerPayment p WHERE p.farm.id = :farmId ORDER BY p.employee.firstName ASC, p.paymentDate DESC")
    List<CasualLabourerPayment> findByFarmIdOrdered(@Param("farmId") Integer farmId);

    Optional<CasualLabourerPayment> findByIdAndFarmId(Integer id, Integer farmId);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM CasualLabourerPayment p WHERE p.employee.id = :employeeId")
    BigDecimal sumAmountByEmployeeId(@Param("employeeId") Integer employeeId);
}
