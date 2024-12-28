package com.medix.medix.repositories;

import com.medix.medix.entities.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class LeaveRepositoryTest {
    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private LeaveRepository leaveRepository;

    private Doctor doctor1;
    private Doctor doctor2;
    private Patient patient;
    private Diagnose diagnose;
    private Appointment appointment;
    private Appointment appointment2;

    @BeforeEach
    void setUp() {
        doctor1 = new Doctor();
        doctor1.setUsername("doctor1");
        doctor1.setFirstName("Петър");
        doctor1.setLastName("Петров");
        doctor1.setPassword("password");
        testEntityManager.persistAndFlush(doctor1);

        doctor2 = new Doctor();
        doctor2.setUsername("doctor2");
        doctor2.setFirstName("Мария");
        doctor2.setLastName("Маринова");
        doctor2.setPassword("password");
        testEntityManager.persistAndFlush(doctor2);

        patient = new Patient();
        patient.setUsername("patient1");
        patient.setFirstName("Иван");
        patient.setLastName("Иванов");
        patient.setPassword("password");
        patient.setGeneralPractitioner(doctor1);
        patient.setEgn("1234567890");
        testEntityManager.persistAndFlush(patient);

        diagnose = new Diagnose();
        diagnose.setName("Грип");
        diagnose.setDescription("Описание");
        testEntityManager.persistAndFlush(diagnose);

        appointment = new Appointment();
        appointment.setDate(LocalDate.now().plusDays(1));
        appointment.setDoctor(doctor1);
        appointment.setPatient(patient);
        appointment.setDiagnose(diagnose);
        testEntityManager.persistAndFlush(appointment);

        appointment2 = new Appointment();
        appointment2.setDate(LocalDate.now().plusDays(2));
        appointment2.setDoctor(doctor2);
        appointment2.setPatient(patient);
        appointment2.setDiagnose(diagnose);
        testEntityManager.persistAndFlush(appointment2);
    }

    @Test
    void findAllByMedicalAppointment_PatientIdTest() {
        Leave sickLeave = new Leave();
        sickLeave.setAppointment(appointment);
        sickLeave.setStartDate(LocalDate.now().plusDays(1));
        sickLeave.setDays(7);
        testEntityManager.persistAndFlush(sickLeave);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Leave> result = leaveRepository.findAllByAppointment_PatientId(patient.getId(), pageable);

        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    void findAllByMedicalAppointment_DoctorIdTest() {
        Leave sickLeave = new Leave();
        sickLeave.setAppointment(appointment);
        sickLeave.setStartDate(LocalDate.now().plusDays(1));
        sickLeave.setDays(7);
        testEntityManager.persistAndFlush(sickLeave);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Leave> result = leaveRepository.findAllByAppointment_DoctorId(doctor1.getId(), pageable);

        assertThat(result.getTotalElements()).isEqualTo(1);
    }
}