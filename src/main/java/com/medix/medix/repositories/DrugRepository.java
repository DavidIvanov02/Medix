package com.medix.medix.repositories;

import com.medix.medix.entities.Drug;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DrugRepository extends JpaRepository<Drug, Long> {
    boolean existsByName(String name);
}