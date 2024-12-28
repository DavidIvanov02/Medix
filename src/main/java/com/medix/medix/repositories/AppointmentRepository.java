package com.medix.medix.repositories;

import com.medix.medix.dtos.doctor.DoctorWithAppointmentCount;
import com.medix.medix.entities.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long>, JpaSpecificationExecutor<Appointment> {
    boolean existsByIdAndDoctorId(Long id, Long doctorId);

    @Query("SELECT new com.medix.medix.dtos.doctor.DoctorWithAppointmentCount(m.doctor, COUNT(m)) " +
            "FROM Appointment m " +
            "GROUP BY m.doctor " +
            "ORDER BY COUNT(m) DESC")
    List<DoctorWithAppointmentCount> countDoctorAppointments();
}