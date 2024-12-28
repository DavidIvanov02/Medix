package com.medix.medix.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "drugs")
@Getter
@Setter
public class Drug extends BaseEntity {
    @NotBlank(message = "Името на лекарството не може да бъде празно")
    private String name;

    @NotNull(message = "Описание на лекарството не може да бъде празно")
    private String description;

    @Min(value = 0, message = "Цената на лекарството не може да бъде отрицателна")
    private Double price;

    @ManyToMany(mappedBy = "drugs")
    private List<Appointment> appointments;
}
