package com.medix.medix.services;

import com.medix.medix.dtos.doctor.DoctorWithAppointmentCount;
import com.medix.medix.dtos.appointment.EditAppointmentRequestDto;
import com.medix.medix.dtos.appointment.CreateAppointmentRequestDto;
import com.medix.medix.dtos.appointment.AppointmentSearchDto;
import com.medix.medix.entities.*;
import com.medix.medix.repositories.*;
import com.medix.medix.specifications.AppointmentSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final DiagnoseRepository diagnoseRepository;
    private final DrugRepository drugRepository;

    public Appointment create(CreateAppointmentRequestDto appointmentDto, BindingResult bindingResult) {
        if (appointmentDto == null) {
            bindingResult.rejectValue("date", "error.appointment", "Грешка при създаване на специалност");
            return null;
        }

        Doctor doctor;
        try {
            doctor = doctorRepository.findById(appointmentDto.getDoctorId()).orElseThrow();
        } catch (Exception e) {
            bindingResult.rejectValue("doctorId", "error.appointment", "Докторът не съществува");
            return null;
        }

        Patient patient;
        try {
            patient = patientRepository.findById(appointmentDto.getPatientId()).orElseThrow();
        } catch (Exception e) {
            bindingResult.rejectValue("patientId", "error.appointment", "Пациентът не съществува");
            return null;
        }

        Diagnose diagnose;
        try {
            diagnose = diagnoseRepository.findById(appointmentDto.getDiagnoseId()).orElseThrow();
        } catch (Exception e) {
            bindingResult.rejectValue("diagnoseId", "error.appointment", "Диагнозата не съществува");
            return null;
        }

        Appointment appointment = new Appointment();
        appointment.setDate(appointmentDto.getDate());
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setDiagnose(diagnose);

        try {
            for (Long drugId : appointmentDto.getDrugs()) {
                Drug drug = drugRepository.findById(drugId)
                        .orElseThrow(() -> new NoSuchElementException("Лекарството не е намерена"));
                appointment.addDrug(drug);
            }
        } catch (NoSuchElementException e) {
            bindingResult.rejectValue("specialities", "error.appointment", "Грешка при добавяне на лекарства");
            return null;
        }

        try {
            return appointmentRepository.save(appointment);
        } catch (Exception e) {
            bindingResult.rejectValue("date", "error.appointment", "Грешка при създаване на специалност");
            return null;
        }
    }

    public Appointment update(Long id, EditAppointmentRequestDto editAppointmentDto, BindingResult bindingResult) {
        if (editAppointmentDto == null) {
            bindingResult.rejectValue("date", "error.appointment", "Грешка при редактиране на специалност");
            return null;
        }

        Appointment appointment;
        try {
            appointment = findById(id);
        } catch (Exception e) {
            bindingResult.rejectValue("date", "error.appointment", "Специалността не съществува");
            return null;
        }

        Doctor doctor;
        try {
            doctor = doctorRepository.findById(editAppointmentDto.getDoctorId()).orElseThrow();
        } catch (Exception e) {
            bindingResult.rejectValue("doctorId", "error.appointment", "Докторът не съществува");
            return null;
        }

        Patient patient;
        try {
            patient = patientRepository.findById(editAppointmentDto.getPatientId()).orElseThrow();
        } catch (Exception e) {
            bindingResult.rejectValue("patientId", "error.appointment", "Пациентът не съществува");
            return null;
        }

        Diagnose diagnose;
        try {
            diagnose = diagnoseRepository.findById(editAppointmentDto.getDiagnoseId()).orElseThrow();
        } catch (Exception e) {
            bindingResult.rejectValue("diagnoseId", "error.appointment", "Диагнозата не съществува");
            return null;
        }

        try {
            appointment.clearDrugs();
            for (Long drugId : editAppointmentDto.getDrugs()) {
                Drug drug = drugRepository.findById(drugId)
                        .orElseThrow(() -> new NoSuchElementException("Лекарството не е намерена"));
                appointment.addDrug(drug);
            }
        } catch (NoSuchElementException e) {
            bindingResult.rejectValue("drugs", "error.appointment", "Грешка при добавяне на лекарства");
            return null;
        }

        appointment.setDate(editAppointmentDto.getDate());
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setDiagnose(diagnose);

        try {
            return appointmentRepository.save(appointment);
        } catch (Exception e) {
            bindingResult.rejectValue("date", "error.appointment", "Грешка при редактиране на специалност");
            return null;
        }
    }

    public Appointment findById(Long id) {
        return appointmentRepository.findById(id).orElseThrow();
    }

    public void deleteById(Long id) {
        appointmentRepository.deleteById(id);
    }

    public boolean isDoctorAppointment(Long appointmentId, Long doctorId) {
        return appointmentRepository.existsByIdAndDoctorId(appointmentId, doctorId);
    }

    public Page<Appointment> findAllBasedOnRole(Pageable pageable, AppointmentSearchDto searchForm) {
        User user = UserServiceImpl.getCurrentUser();
        Specification<Appointment> specification = buildRoleBasedSpecification(user, searchForm);

        specification = specification
                .and(AppointmentSpecification.hasDiagnoseId(searchForm.getDiagnoseId()))
                .and(AppointmentSpecification.hasDoctorId(searchForm.getDoctorId()))
                .and(AppointmentSpecification.hasPatientId(searchForm.getPatientId()))
                .and(AppointmentSpecification.hasStartDateOnOrAfter(searchForm.getStartDate()))
                .and(AppointmentSpecification.hasEndDateOnOrBefore(searchForm.getEndDate()));

        return appointmentRepository.findAll(specification, pageable);
    }

    public List<Appointment> findAllBasedOnRole() {
        User user = UserServiceImpl.getCurrentUser();

        Specification<Appointment> specification = Specification.where(AppointmentSpecification.hasDiagnoseId(null));
        if (user.isDoctor()) {
            specification = specification.and(AppointmentSpecification.hasDoctorId(user.getId()));
        }

        if (user.isPatient()) {
            specification = specification.and(AppointmentSpecification.hasPatientId(user.getId()));
        }

        return appointmentRepository.findAll(specification);
    }

    private Specification<Appointment> buildRoleBasedSpecification(User user, AppointmentSearchDto searchForm) {
        Specification<Appointment> specification = Specification.where(null);

        if (user.isDoctor()) {
            specification = Specification.where(AppointmentSpecification.hasDoctorId(user.getId()));
        } else if (user.isPatient()) {
            specification = Specification.where(AppointmentSpecification.hasPatientId(user.getId()));
        }

        return specification;
    }

    public List<DoctorWithAppointmentCount> countDoctorAppointments() {
        return appointmentRepository.countDoctorAppointments();
    }
}
