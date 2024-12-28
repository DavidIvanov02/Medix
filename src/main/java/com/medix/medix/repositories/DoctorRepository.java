package com.medix.medix.repositories;

import com.medix.medix.entities.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    List<Doctor> findAllByIsGeneralPractitioner(boolean isGeneralPractitioner);
}