package com.medix.medix.controllers;

import com.medix.medix.dtos.doctor.CreateDoctorRequestDto;
import com.medix.medix.dtos.doctor.EditDoctorRequestDto;
import com.medix.medix.entities.Doctor;
import com.medix.medix.services.DoctorService;
import com.medix.medix.services.SpecialityService;
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

@RequestMapping("/doctors")
@RequiredArgsConstructor
@Controller
public class DoctorController {
    private final DoctorService doctorService;
    private final SpecialityService specialityService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_DOCTOR')")
    public String index(Model model, Pageable pageable) {
        model.addAttribute("doctors", doctorService.findAll(pageable));

        return "doctors/index";
    }

    @GetMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public String create(Model model) {
        CreateDoctorRequestDto doctor = new CreateDoctorRequestDto();
        model.addAttribute("doctor", doctor);
        model.addAttribute("specialities", specialityService.findAll());

        return "doctors/create";
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public String store(@Valid @ModelAttribute("doctor") CreateDoctorRequestDto doctor, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("specialities", specialityService.findAll());

            return "doctors/create";
        }

        doctorService.create(doctor, bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("specialities", specialityService.findAll());

            return "doctors/create";
        }

        return "redirect:/doctors";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public String edit(@PathVariable Long id, Model model) {
        try {
            Doctor doctor = doctorService.findById(id);
            model.addAttribute("doctor", new EditDoctorRequestDto(doctor));
            model.addAttribute("specialities", specialityService.findAll());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Докторът не съществува.");
        }

        return "doctors/edit";
    }

    @PostMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public String update(@PathVariable Long id, @Valid @ModelAttribute("doctor") EditDoctorRequestDto doctor, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("specialities", specialityService.findAll());

            return "doctors/edit";
        }

        doctorService.update(id, doctor, bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("specialities", specialityService.findAll());

            return "doctors/edit";
        }

        return "redirect:/doctors";
    }
}
