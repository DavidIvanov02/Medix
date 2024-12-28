package com.medix.medix.repositories;

import com.medix.medix.entities.Insurance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface InsuranceRepository extends JpaRepository<Insurance, Long> {
    boolean existsByPatientIdAndInsuranceDateAndIdNot(Long patientId, LocalDate insuranceDate, Long Id);

    boolean existsByPatientIdAndInsuranceDate(Long patientId, LocalDate insuranceDate);

    Page<Insurance> findAllByPatientId(Long patientId, Pageable pageable);

    Page<Insurance> findAllByPatient_GeneralPractitionerId(Long generalPractitionerId, Pageable pageable);

    boolean existsByIdAndPatientId(Long id, Long patientId);

    List<Insurance> findAllByPatientIdAndInsuranceDateBetween(Long patientId, LocalDate startDate, LocalDate endDate);
}