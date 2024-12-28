package com.medix.medix.dtos.doctor;

import com.medix.medix.dtos.user.CreateBaseUserRequestDto;
import com.medix.medix.enums.Role;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateDoctorRequestDto extends CreateBaseUserRequestDto {
    private Boolean isGeneralPractitioner;

    private List<Long> specialities;

    public CreateDoctorRequestDto() {
        super();
        setRole(Role.ROLE_DOCTOR);
    }
}
