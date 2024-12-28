package com.medix.medix.repositories;

import com.medix.medix.entities.Drug;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class DrugRepositoryTest {
    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private DrugRepository drugRepository;

    @Test
    void existsByNameTest() {
        Drug drug = new Drug();
        drug.setName("Аспирин");
        drug.setDescription("Обезболяващо средство");
        drug.setPrice(5.50);
        testEntityManager.persistAndFlush(drug);

        boolean exists = drugRepository.existsByName("Аспирин");

        assertThat(exists).isTrue();
    }

    @Test
    void existsByNameNotFoundTest() {
        boolean exists = drugRepository.existsByName("Несъществуващо лекарство");

        assertThat(exists).isFalse();
    }
}