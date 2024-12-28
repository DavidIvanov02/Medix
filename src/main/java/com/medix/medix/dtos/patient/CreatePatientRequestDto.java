package com.medix.medix.dtos.patient;

import com.medix.medix.annotations.Unique;
import com.medix.medix.dtos.user.CreateBaseUserRequestDto;
import com.medix.medix.entities.Patient;
import com.medix.medix.enums.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Unique(entityClass = Patient.class, fieldName = "egn", message = "ЕГН-то вече съществува.")
public class CreatePatientRequestDto extends CreateBaseUserRequestDto {
    @NotBlank(message = "ЕГН-то е задължително.")
    private String egn;

    @NotNull(message = "Личният лекар е задължителен.")
    private Long generalPractitionerId;

    public CreatePatientRequestDto() {
        super();
        setRole(Role.ROLE_PATIENT);
    }
}
