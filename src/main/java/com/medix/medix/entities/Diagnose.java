package com.medix.medix.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.util.List;

@Entity
@Table(name = "diagnoses")
@Getter
@Setter
public class Diagnose extends BaseEntity {
    @NotBlank(message = "Името на диагнозата не може да бъде празно")
    private String name;

    @NotBlank(message = "Описанието на диагнозата не може да бъде празно")
    @Length(max = 255, message = "Описанието на диагнозата трябва да бъде по-малко от 255 символа")
    private String description;

    @OneToMany(mappedBy = "diagnose")
    private List<Appointment> appointments;
}
