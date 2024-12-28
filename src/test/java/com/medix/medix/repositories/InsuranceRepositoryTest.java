package com.medix.medix.repositories;

import com.medix.medix.entities.Doctor;
import com.medix.medix.entities.Insurance;
import com.medix.medix.entities.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class InsuranceRepositoryTest {
    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private InsuranceRepository insuranceRepository;

    private Patient patient1;
    private Patient patient2;
    private Doctor doctor;

    @BeforeEach
    void setUp() {
        doctor = new Doctor();
        doctor.setUsername("doctor1");
        doctor.setFirstName("Петър");
        doctor.setLastName("Петров");
        doctor.setPassword("password");
        testEntityManager.persistAndFlush(doctor);

        patient1 = new Patient();
        patient1.setUsername("patient1");
        patient1.setFirstName("Иван");
        patient1.setLastName("Иванов");
        patient1.setPassword("password");
        patient1.setEgn("1234567890");
        patient1.setGeneralPractitioner(doctor);
        testEntityManager.persistAndFlush(patient1);

        patient2 = new Patient();
        patient2.setUsername("patient2");
        patient2.setFirstName("Георги");
        patient2.setLastName("Георгиев");
        patient2.setPassword("password");
        patient2.setEgn("0987654321");
        patient2.setGeneralPractitioner(doctor);
        testEntityManager.persistAndFlush(patient2);
    }

    @Test
    void existsByPatientIdAndInsuranceDateAndIdNotTest() {
        Insurance insurance = new Insurance();
        insurance.setPatient(patient1);
        insurance.setInsuranceDate(LocalDate.of(2024, 1, 1));
        insurance.setDateOfPayment(LocalDate.of(2024, 1, 2));
        insurance.setSum(100.0);
        testEntityManager.persistAndFlush(insurance);

        boolean exists = insuranceRepository.existsByPatientIdAndInsuranceDateAndIdNot(patient1.getId(), LocalDate.of(2024, 1, 1), 999L);

        assertThat(exists).isTrue();
    }

    @Test
    void existsByPatientIdAndInsuranceDateTest() {
        Insurance insurance = new Insurance();
        insurance.setPatient(patient2);
        insurance.setInsuranceDate(LocalDate.of(2024, 2, 1));
        insurance.setDateOfPayment(LocalDate.of(2024, 2, 2));
        insurance.setSum(200.0);
        testEntityManager.persistAndFlush(insurance);

        boolean exists = insuranceRepository.existsByPatientIdAndInsuranceDate(patient2.getId(), LocalDate.of(2024, 2, 1));

        assertThat(exists).isTrue();
    }

    @Test
    void findAllByPatientIdTest() {
        Insurance insurance1 = new Insurance();
        insurance1.setPatient(patient1);
        insurance1.setInsuranceDate(LocalDate.of(2024, 3, 1));
        insurance1.setDateOfPayment(LocalDate.of(2024, 3, 2));
        insurance1.setSum(300.0);
        testEntityManager.persistAndFlush(insurance1);

        Insurance insurance2 = new Insurance();
        insurance2.setPatient(patient1);
        insurance2.setInsuranceDate(LocalDate.of(2024, 3, 15));
        insurance2.setDateOfPayment(LocalDate.of(2024, 3, 16));
        insurance2.setSum(400.0);
        testEntityManager.persistAndFlush(insurance2);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Insurance> insurances = insuranceRepository.findAllByPatientId(patient1.getId(), pageable);

        assertThat(insurances.getTotalElements()).isEqualTo(2);
    }

    @Test
    void findAllByPatient_GeneralPractitionerIdTest() {
        Insurance insurance = new Insurance();
        insurance.setPatient(patient2);
        insurance.setInsuranceDate(LocalDate.of(2024, 4, 1));
        insurance.setDateOfPayment(LocalDate.of(2024, 4, 2));
        insurance.setSum(500.0);
        testEntityManager.persistAndFlush(insurance);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Insurance> insurances = insuranceRepository.findAllByPatient_GeneralPractitionerId(doctor.getId(), pageable);

        assertThat(insurances.getTotalElements()).isEqualTo(1);
    }

    @Test
    void findAllByPatientIdAndInsuranceDateBetweenTest() {
        Insurance insurance1 = new Insurance();
        insurance1.setPatient(patient1);
        insurance1.setInsuranceDate(LocalDate.of(2024, 5, 1));
        insurance1.setDateOfPayment(LocalDate.of(2024, 5, 2));
        insurance1.setSum(600.0);
        testEntityManager.persistAndFlush(insurance1);

        Insurance insurance2 = new Insurance();
        insurance2.setPatient(patient1);
        insurance2.setInsuranceDate(LocalDate.of(2024, 5, 15));
        insurance2.setDateOfPayment(LocalDate.of(2024, 5, 16));
        insurance2.setSum(700.0);
        testEntityManager.persistAndFlush(insurance2);

        List<Insurance> insurances = insuranceRepository.findAllByPatientIdAndInsuranceDateBetween(patient1.getId(), LocalDate.of(2024, 5, 1), LocalDate.of(2024, 5, 31));

        assertThat(insurances).hasSize(2);
    }
}
