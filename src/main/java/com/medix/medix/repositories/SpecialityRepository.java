package com.medix.medix.repositories;

import com.medix.medix.entities.Speciality;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpecialityRepository extends JpaRepository<Speciality, Long> {
    boolean existsByName(String name);
}