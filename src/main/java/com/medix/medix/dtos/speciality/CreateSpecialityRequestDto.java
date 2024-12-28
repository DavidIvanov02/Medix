package com.medix.medix.dtos.speciality;

import com.medix.medix.annotations.Unique;
import com.medix.medix.entities.Speciality;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Unique(entityClass = Speciality.class, fieldName = "name", message = "Специалността вече съществува.")
public class CreateSpecialityRequestDto {
    @NotBlank(message = "Името е задължително.")
    private String name;
}