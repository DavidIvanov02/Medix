package com.medix.medix.dtos.diagnose;

import com.medix.medix.annotations.Unique;
import com.medix.medix.entities.Diagnose;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@Unique(entityClass = Diagnose.class, fieldName = "name", message = "Диагнозата вече съществува.")
public class EditDiagnoseRequestDto {
    private Long id;

    @NotBlank(message = "Името е задължително.")
    private String name;

    @NotBlank(message = "Описанието е задължително.")
    @Length(max = 255, message = "Описанието на диагнозата трябва да бъде по-малко от 255 символа.")
    private String description;

    public EditDiagnoseRequestDto() {
    }

    public EditDiagnoseRequestDto(Diagnose diagnose) {
        id = diagnose.getId();
        name = diagnose.getName();
        description = diagnose.getDescription();
    }
}