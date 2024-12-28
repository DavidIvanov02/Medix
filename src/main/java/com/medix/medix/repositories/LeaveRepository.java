package com.medix.medix.repositories;

import com.medix.medix.entities.Leave;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeaveRepository extends JpaRepository<Leave, Long> {
    Page<Leave> findAllByAppointment_PatientId(Long patientId, Pageable pageable);

    Page<Leave> findAllByAppointment_DoctorId(Long doctorId, Pageable pageable);
}