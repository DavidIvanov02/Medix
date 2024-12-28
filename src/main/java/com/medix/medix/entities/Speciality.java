package com.medix.medix.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "specialities")
@Getter
@Setter
public class Speciality extends BaseEntity {
    @Column(nullable = false, unique = true)
    @NotBlank(message = "Името е задължително")
    private String name;

    @ManyToMany(mappedBy = "specialities")
    Set<Doctor> doctors;
}
