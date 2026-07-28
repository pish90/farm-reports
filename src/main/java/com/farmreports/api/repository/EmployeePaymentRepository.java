package com.farmreports.api.repository;

import com.farmreports.api.entity.EmployeePayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EmployeePaymentRepository extends JpaRepository<EmployeePayment, Integer> {

    List<EmployeePayment> findByEmployeeIdAndFarmIdOrderByPaymentDateDesc(Integer employeeId, Integer farmId);

    Optional<EmployeePayment> findByIdAndFarmId(Integer id, Integer farmId);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM EmployeePayment p WHERE p.employeeId = :employeeId AND p.farmId = :farmId")
    BigDecimal sumAmountByEmployeeIdAndFarmId(Integer employeeId, Integer farmId);

    List<EmployeePayment> findByEmployeeIdAndFarmIdAndPaymentDateBetween(
            Integer employeeId, Integer farmId, LocalDate start, LocalDate end);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM EmployeePayment p " +
           "WHERE p.employeeId = :employeeId AND p.farmId = :farmId AND p.paymentDate < :before")
    BigDecimal sumAmountBeforeDate(@Param("employeeId") Integer employeeId, @Param("farmId") Integer farmId, @Param("before") LocalDate before);
}
