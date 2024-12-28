package com.medix.medix.repositories;

import com.medix.medix.entities.Speciality;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class SpecialityRepositoryTest {
    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private SpecialityRepository specialityRepository;

    private Speciality speciality;

    @BeforeEach
    void setUp() {
        speciality = new Speciality();
        speciality.setName("Кардиология");
        testEntityManager.persistAndFlush(speciality);
    }

    @Test
    void existsByNameTest() {
        boolean exists = specialityRepository.existsByName("Кардиология");

        assertThat(exists).isTrue();
    }

    @Test
    void existsByNameNotFoundTest() {
        boolean exists = specialityRepository.existsByName("Дерматология");

        assertThat(exists).isFalse();
    }

    @Test
    void saveAndRetrieveSpecialityTest() {
        Speciality newSpeciality = new Speciality();
        newSpeciality.setName("Дерматология");
        testEntityManager.persistAndFlush(newSpeciality);

        Speciality retrievedSpeciality = specialityRepository.findById(newSpeciality.getId()).orElse(null);

        assertThat(retrievedSpeciality).isNotNull();
        assertThat(retrievedSpeciality.getName()).isEqualTo("Дерматология");
    }
}
