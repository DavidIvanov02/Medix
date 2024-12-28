package com.medix.medix.controllers;

import com.medix.medix.dtos.appointment.EditAppointmentRequestDto;
import com.medix.medix.dtos.appointment.CreateAppointmentRequestDto;
import com.medix.medix.dtos.appointment.AppointmentSearchDto;
import com.medix.medix.entities.Appointment;
import com.medix.medix.services.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RequestMapping("/appointments")
@Controller
@RequiredArgsConstructor
public class AppointmentController {
    private final AppointmentService appointmentService;
    private final DoctorService doctorService;
    private final PatientService patientService;
    private final DiagnoseService diagnoseService;
    private final DrugService drugService;

    @GetMapping
    public String index(Model model, Pageable pageable, @ModelAttribute("searchForm") AppointmentSearchDto searchForm) {
        model.addAttribute("appointments", appointmentService.findAllBasedOnRole(pageable, searchForm));
        model.addAttribute("searchForm", searchForm);
        model.addAttribute("diagnoses", diagnoseService.findAllOrderedByName());
        model.addAttribute("doctors", doctorService.findAllGeneralPractitioners());
        model.addAttribute("patients", patientService.findAll());

        return "appointments/index";
    }

    @GetMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public String create(Model model) {
        CreateAppointmentRequestDto appointment = new CreateAppointmentRequestDto();

        model.addAttribute("appointment", appointment);
        model.addAttribute("doctors", doctorService.findAll());
        model.addAttribute("patients", patientService.findAll());
        model.addAttribute("diagnoses", diagnoseService.findAll());
        model.addAttribute("drugs", drugService.findAll());

        return "appointments/create";
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public String store(@Valid @ModelAttribute("appointment") CreateAppointmentRequestDto appointment, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("doctors", doctorService.findAll());
            model.addAttribute("patients", patientService.findAll());
            model.addAttribute("diagnoses", diagnoseService.findAll());
            model.addAttribute("drugs", drugService.findAll());

            return "appointments/create";
        }

        appointmentService.create(appointment, bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("doctors", doctorService.findAll());
            model.addAttribute("patients", patientService.findAll());
            model.addAttribute("diagnoses", diagnoseService.findAll());
            model.addAttribute("drugs", drugService.findAll());

            return "appointments/create";
        }

        return "redirect:/appointments";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('DOCTOR') and @appointmentService.isDoctorAppointment(#id, principal.id))")
    public String edit(@PathVariable Long id, Model model) {
        try {
            Appointment appointment = appointmentService.findById(id);

            model.addAttribute("appointment", new EditAppointmentRequestDto(appointment));
            model.addAttribute("doctors", doctorService.findAll());
            model.addAttribute("patients", patientService.findAll());
            model.addAttribute("diagnoses", diagnoseService.findAll());
            model.addAttribute("drugs", drugService.findAll());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Посещението не съществува.");
        }

        return "appointments/edit";
    }

    @PostMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('DOCTOR') and @appointmentService.isDoctorAppointment(#id, principal.id))")
    public String update(@PathVariable Long id, @Valid @ModelAttribute("appointment") EditAppointmentRequestDto appointment, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("doctors", doctorService.findAll());
            model.addAttribute("patients", patientService.findAll());
            model.addAttribute("diagnoses", diagnoseService.findAll());
            model.addAttribute("drugs", drugService.findAll());

            return "appointments/edit";
        }

        appointmentService.update(id, appointment, bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("doctors", doctorService.findAll());
            model.addAttribute("patients", patientService.findAll());
            model.addAttribute("diagnoses", diagnoseService.findAll());
            model.addAttribute("drugs", drugService.findAll());

            return "appointments/edit";
        }

        return "redirect:/appointments";
    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public String delete(@PathVariable Long id) {
        try {
            appointmentService.deleteById(id);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Посещението не съществува.");
        }

        return "redirect:/appointments";
    }

    @GetMapping("/doctor_appointments_count")
    @PreAuthorize("hasRole('PATIENT')")
    public String doctorAppointmentsCount(Model model) {
        model.addAttribute("doctorsWithAppointmentCount", appointmentService.countDoctorAppointments());

        return "appointments/doctor_appointments_count";
    }
}
