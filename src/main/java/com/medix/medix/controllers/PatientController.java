package com.medix.medix.controllers;

import com.medix.medix.dtos.patient.EditPatientRequestDto;
import com.medix.medix.dtos.patient.CreatePatientRequestDto;
import com.medix.medix.dtos.patient.PatientSearchDto;
import com.medix.medix.entities.Doctor;
import com.medix.medix.entities.Patient;
import com.medix.medix.entities.User;
import com.medix.medix.services.DoctorService;
import com.medix.medix.services.PatientService;
import com.medix.medix.services.UserServiceImpl;
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

@RequestMapping("/patients")
@RequiredArgsConstructor
@Controller
public class PatientController {
    private final PatientService patientService;
    private final DoctorService doctorService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_DOCTOR')")
    public String index(Model model, Pageable pageable, @ModelAttribute("searchForm") PatientSearchDto searchForm) {
        User user = UserServiceImpl.getCurrentUser();

        boolean isGeneralPractitioner = false;
        if (user.isDoctor()) {
            isGeneralPractitioner = ((Doctor) user).getIsGeneralPractitioner();
        }

        model.addAttribute("isGeneralPractitioner", isGeneralPractitioner);
        model.addAttribute("patients", patientService.findAll(pageable, searchForm));
        model.addAttribute("searchForm", searchForm);
        model.addAttribute("generalPractitioners", doctorService.findAllGeneralPractitioners());

        return "patients/index";
    }

    @GetMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public String create(Model model) {
        model.addAttribute("generalPractitioners", doctorService.findAllGeneralPractitioners());
        model.addAttribute("patient", new CreatePatientRequestDto());

        return "patients/create";
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public String store(@Valid @ModelAttribute("patient") CreatePatientRequestDto patient, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("generalPractitioners", doctorService.findAllGeneralPractitioners());

            return "patients/create";
        }

        patientService.save(patient, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("generalPractitioners", doctorService.findAllGeneralPractitioners());

            return "patients/create";
        }

        return "redirect:/patients";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String edit(@PathVariable Long id, Model model) {
        try {
            Patient patient = patientService.findById(id);
            model.addAttribute("patient", new EditPatientRequestDto(patient));
            model.addAttribute("generalPractitioners", doctorService.findAllGeneralPractitioners());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пациентът не съществува.");
        }

        return "patients/edit";
    }

    @PostMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String update(@PathVariable Long id, @Valid @ModelAttribute("patient") EditPatientRequestDto patient, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("generalPractitioners", doctorService.findAllGeneralPractitioners());

            return "patients/edit";
        }

        patientService.update(id, patient, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("generalPractitioners", doctorService.findAllGeneralPractitioners());

            return "patients/edit";
        }

        return "redirect:/patients";
    }
}
