package com.medix.medix.dtos.drug;

import com.medix.medix.annotations.Unique;
import com.medix.medix.entities.Drug;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@Unique(entityClass = Drug.class, fieldName = "name", message = "Лекарството вече съществува.")
public class CreateDrugRequestDto {
    @NotBlank(message = "Името е задължително.")
    private String name;

    @NotBlank(message = "Описанието е задължително.")
    @Length(max = 255, message = "Описанието на лекарството трябва да бъде по-малко от 255 символа.")
    private String description;

    @Min(value = 0, message = "Цената на лекарството не може да бъде отрицателна.")
    private Double price;
}