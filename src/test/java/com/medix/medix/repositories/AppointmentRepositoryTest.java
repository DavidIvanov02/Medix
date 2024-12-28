package com.medix.medix.repositories;

import com.medix.medix.dtos.doctor.DoctorWithAppointmentCount;
import com.medix.medix.entities.Diagnose;
import com.medix.medix.entities.Doctor;
import com.medix.medix.entities.Appointment;
import com.medix.medix.entities.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class AppointmentRepositoryTest {
    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private AppointmentRepository medicalAppointmentRepository;

    private Doctor doctor1;
    private Doctor doctor2;
    private Patient patient;
    private Diagnose diagnose;

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
        diagnose.setDescription("Вирусно заболяване");
        testEntityManager.persistAndFlush(diagnose);
    }

    @Test
    void existsByIdAndDoctorIdTest() {
        Appointment appointment = new Appointment();
        appointment.setDate(LocalDate.now().plusDays(1));
        appointment.setDoctor(doctor1);
        appointment.setPatient(patient);
        appointment.setDiagnose(diagnose);
        testEntityManager.persistAndFlush(appointment);

        boolean exists = medicalAppointmentRepository.existsByIdAndDoctorId(appointment.getId(), doctor1.getId());

        assertThat(exists).isTrue();
    }

    @Test
    void countDoctorAppointmentsTest() {
        Appointment appointment1 = new Appointment();
        appointment1.setDate(LocalDate.now().plusDays(1));
        appointment1.setDoctor(doctor1);
        appointment1.setPatient(patient);
        appointment1.setDiagnose(diagnose);
        testEntityManager.persistAndFlush(appointment1);

        Appointment appointment2 = new Appointment();
        appointment2.setDate(LocalDate.now().plusDays(2));
        appointment2.setDoctor(doctor1);
        appointment2.setPatient(patient);
        appointment2.setDiagnose(diagnose);
        testEntityManager.persistAndFlush(appointment2);

        Appointment appointment3 = new Appointment();
        appointment3.setDate(LocalDate.now().plusDays(3));
        appointment3.setDoctor(doctor2);
        appointment3.setPatient(patient);
        appointment3.setDiagnose(diagnose);
        testEntityManager.persistAndFlush(appointment3);

        List<DoctorWithAppointmentCount> result = medicalAppointmentRepository.countDoctorAppointments();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getDoctor().getId()).isEqualTo(doctor1.getId());
        assertThat(result.get(0).getAppointmentCount()).isEqualTo(2);
        assertThat(result.get(1).getDoctor().getId()).isEqualTo(doctor2.getId());
        assertThat(result.get(1).getAppointmentCount()).isEqualTo(1);
    }
}