package com.medix.medix.entities;

import com.medix.medix.dtos.doctor.CreateDoctorRequestDto;
import com.medix.medix.enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "doctors")
@PrimaryKeyJoinColumn(name = "user_id")
@Getter
@Setter
public class Doctor extends User implements Serializable {
    @Column(nullable = false)
    private Boolean isGeneralPractitioner = false;

    @ManyToMany
    @JoinTable(name = "doctor_specialities", joinColumns = @JoinColumn(name = "doctor_id"), inverseJoinColumns = @JoinColumn(name = "speciality_id"))
    @OrderBy("name ASC")
    private List<Speciality> specialities = new ArrayList<>();

    @OneToMany(mappedBy = "doctor")
    @LazyCollection(LazyCollectionOption.EXTRA)
    private List<Appointment> appointments = new ArrayList<>();

    @OneToMany(mappedBy = "generalPractitioner")
    @LazyCollection(LazyCollectionOption.EXTRA)
    private List<Patient> patients = new ArrayList<>();

    @Formula("(SELECT COUNT(*) FROM patients p WHERE p.general_practitioner_id = user_id)")
    private Long patientsCount;

    @Formula("(SELECT COUNT(*) FROM appointments m WHERE m.doctor_id = user_id)")
    private Long appointmentsCount;

    public Doctor() {
        setRole(Role.ROLE_DOCTOR);
    }

    public Doctor(CreateDoctorRequestDto doctorRequestDto) {
        super(doctorRequestDto);
        isGeneralPractitioner = doctorRequestDto.getIsGeneralPractitioner();
    }

    public void addSpeciality(Speciality speciality) {
        specialities.add(speciality);
    }

    public void clearSpecialities() {
        specialities.clear();
    }
}
