package com.medix.medix.dtos.diagnose;

import com.medix.medix.entities.Diagnose;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DiagnoseAppointmentCount {
    private Diagnose diagnose;
    private long appointmentCount;

    public DiagnoseAppointmentCount(Diagnose diagnose, long appointmentCount) {
        this.diagnose = diagnose;
        this.appointmentCount = appointmentCount;
    }
}
