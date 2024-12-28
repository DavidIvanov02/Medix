package com.medix.medix.dtos.doctor;

import com.medix.medix.entities.Doctor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DoctorWithAppointmentCount {
    private Doctor doctor;
    private Long appointmentCount;

    public DoctorWithAppointmentCount(Doctor doctor, Long appointmentCount) {
        this.doctor = doctor;
        this.appointmentCount = appointmentCount;
    }
}
