package com.medix.medix.dtos.appointment;

import com.medix.medix.dtos.user.BaseUserSearchDto;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
public class AppointmentSearchDto extends BaseUserSearchDto {
    private Long diagnoseId;
    private Long doctorId;
    private Long patientId;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
}
