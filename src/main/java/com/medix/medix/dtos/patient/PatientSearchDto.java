package com.medix.medix.dtos.patient;

import com.medix.medix.dtos.user.BaseUserSearchDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PatientSearchDto extends BaseUserSearchDto {
    private Long generalPractitionerId;
}
