package com.medix.medix.repositories;

import com.medix.medix.entities.Doctor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class DoctorRepositoryTest {
    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private DoctorRepository doctorRepository;

    @Test
    void findAllByIsGeneralPractitionerTest() {
        Doctor doctor1 = new Doctor();
        doctor1.setUsername("doktor1");
        doctor1.setFirstName("Иван");
        doctor1.setLastName("Иванов");
        doctor1.setPassword("password");
        doctor1.setIsGeneralPractitioner(true);
        testEntityManager.persistAndFlush(doctor1);

        Doctor doctor2 = new Doctor();
        doctor2.setUsername("doktor2");
        doctor2.setFirstName("Георги");
        doctor2.setLastName("Георгиев");
        doctor2.setPassword("password");
        doctor2.setIsGeneralPractitioner(false);
        testEntityManager.persistAndFlush(doctor2);

        List<Doctor> generalPractitioners = doctorRepository.findAllByIsGeneralPractitioner(true);

        assertThat(generalPractitioners).hasSize(1);
        assertThat(generalPractitioners.get(0).getUsername()).isEqualTo("doktor1");
        assertThat(generalPractitioners.get(0).getIsGeneralPractitioner()).isTrue();
    }

    @Test
    void findAllByIsGeneralPractitionerFalseTest() {
        Doctor doctor1 = new Doctor();
        doctor1.setUsername("doktor1");
        doctor1.setFirstName("Иван");
        doctor1.setLastName("Иванов");
        doctor1.setPassword("password");
        doctor1.setIsGeneralPractitioner(true);
        testEntityManager.persistAndFlush(doctor1);

        Doctor doctor2 = new Doctor();
        doctor2.setUsername("doktor2");
        doctor2.setFirstName("Георги");
        doctor2.setLastName("Георгиев");
        doctor2.setPassword("password");
        doctor2.setIsGeneralPractitioner(false);
        testEntityManager.persistAndFlush(doctor2);

        List<Doctor> nonGeneralPractitioners = doctorRepository.findAllByIsGeneralPractitioner(false);

        assertThat(nonGeneralPractitioners).hasSize(1);
        assertThat(nonGeneralPractitioners.get(0).getUsername()).isEqualTo("doktor2");
        assertThat(nonGeneralPractitioners.get(0).getIsGeneralPractitioner()).isFalse();
    }
}

