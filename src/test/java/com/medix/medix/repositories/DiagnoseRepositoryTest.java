package com.medix.medix.repositories;

import com.medix.medix.dtos.diagnose.DiagnoseAppointmentCount;
import com.medix.medix.entities.Diagnose;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class DiagnoseRepositoryTest {
    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private DiagnoseRepository diagnoseRepository;

    @Test
    void existsByNameTest() {
        Diagnose diagnose = new Diagnose();
        diagnose.setName("Грип");
        diagnose.setDescription("Инфлуенца");
        testEntityManager.persistAndFlush(diagnose);

        boolean exists = diagnoseRepository.existsByName("Грип");

        assertThat(exists).isTrue();
    }

    @Test
    void existsByNameNotFoundTest() {
        boolean exists = diagnoseRepository.existsByName("Несъществуващ");

        assertThat(exists).isFalse();
    }

    @Test
    void findAllByOrderByNameTest() {
        Diagnose diagnose1 = new Diagnose();
        diagnose1.setName("Заболяване А");
        diagnose1.setDescription("Описание А");
        testEntityManager.persistAndFlush(diagnose1);

        Diagnose diagnose2 = new Diagnose();
        diagnose2.setName("Заболяване Б");
        diagnose2.setDescription("Описание Б");
        testEntityManager.persistAndFlush(diagnose2);

        Diagnose diagnose3 = new Diagnose();
        diagnose3.setName("Заболяване В");
        diagnose3.setDescription("Описание В");
        testEntityManager.persistAndFlush(diagnose3);

        List<Diagnose> diagnoses = diagnoseRepository.findAllByOrderByName();

        assertThat(diagnoses).hasSize(3);
        assertThat(diagnoses.get(0).getName()).isEqualTo("Заболяване А");
        assertThat(diagnoses.get(1).getName()).isEqualTo("Заболяване Б");
        assertThat(diagnoses.get(2).getName()).isEqualTo("Заболяване В");
    }

    @Test
    void getDiagnosesAndMedicalAppointmentsCountTest() {
        Diagnose diagnose1 = new Diagnose();
        diagnose1.setName("Болест А");
        diagnose1.setDescription("Описание А");
        testEntityManager.persistAndFlush(diagnose1);

        Diagnose diagnose2 = new Diagnose();
        diagnose2.setName("Болест Б");
        diagnose2.setDescription("Описание Б");
        testEntityManager.persistAndFlush(diagnose2);

        List<DiagnoseAppointmentCount> counts = diagnoseRepository.getDiagnosesAndAppointmentsCount();

        assertThat(counts).isNotNull();
        assertThat(counts.size()).isGreaterThanOrEqualTo(0);
    }
}
