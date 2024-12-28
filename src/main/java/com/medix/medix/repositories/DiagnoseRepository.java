package com.medix.medix.repositories;

import com.medix.medix.dtos.diagnose.DiagnoseAppointmentCount;
import com.medix.medix.entities.Diagnose;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DiagnoseRepository extends JpaRepository<Diagnose, Long> {
    boolean existsByName(String name);

    List<Diagnose> findAllByOrderByName();

    @Query("SELECT new com.medix.medix.dtos.diagnose.DiagnoseAppointmentCount(d, COUNT(ma)) " +
            "FROM Diagnose d JOIN d.appointments ma " +
            "GROUP BY d ORDER BY COUNT(ma) DESC")
    List<DiagnoseAppointmentCount> getDiagnosesAndAppointmentsCount();
}