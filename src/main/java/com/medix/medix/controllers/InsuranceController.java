package com.medix.medix.controllers;

import com.medix.medix.dtos.insurance.EditInsuranceRequestDto;
import com.medix.medix.dtos.insurance.CreateInsuranceRequestDto;
import com.medix.medix.entities.Insurance;
import com.medix.medix.entities.User;
import com.medix.medix.services.InsuranceService;
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

@RequestMapping("/insurances")
@Controller
@RequiredArgsConstructor
public class InsuranceController {
    private final PatientService patientService;
    private final InsuranceService insuranceService;

    @GetMapping
    public String index(Model model, Pageable pageable) {
        model.addAttribute("insurances", insuranceService.findAllBasedOnRole(pageable));

        return "insurances/index";
    }

    @GetMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN', 'PATIENT')")
    public String create(Model model) {
        User user = UserServiceImpl.getCurrentUser();

        CreateInsuranceRequestDto insurance = new CreateInsuranceRequestDto();
        if (user.isPatient()) {
            insurance.setPatientId(user.getId());
        }

        model.addAttribute("isPatient", user.isPatient());
        model.addAttribute("insurance", insurance);
        model.addAttribute("patients", patientService.findAll());

        return "insurances/create";
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN', 'PATIENT')")
    public String store(@Valid @ModelAttribute("insurance") CreateInsuranceRequestDto insurance, BindingResult bindingResult, Model model) {
        User user = UserServiceImpl.getCurrentUser();

        if (bindingResult.hasErrors()) {
            model.addAttribute("isPatient", user.isPatient());
            model.addAttribute("patients", patientService.findAll());

            return "insurances/create";
        }

        insuranceService.create(insurance, bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("isPatient", user.isPatient());
            model.addAttribute("patients", patientService.findAll());

            return "insurances/create";
        }

        return "redirect:/insurances";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('PATIENT') and @insuranceService.isPatientInsurance(#id, principal.id))")
    public String edit(@PathVariable Long id, Model model) {
        User user = UserServiceImpl.getCurrentUser();

        try {
            Insurance insurance = insuranceService.findById(id);

            model.addAttribute("insurance", new EditInsuranceRequestDto(insurance));
            model.addAttribute("patients", patientService.findAll());
            model.addAttribute("isPatient", user.isPatient());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Осигуровката не съществува.");
        }

        return "insurances/edit";
    }

    @PostMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('PATIENT') and @insuranceService.isPatientInsurance(#id, principal.id))")
    public String update(@PathVariable Long id, @Valid @ModelAttribute("insurance") EditInsuranceRequestDto insurance, BindingResult bindingResult, Model model) {
        User user = UserServiceImpl.getCurrentUser();

        if (bindingResult.hasErrors()) {
            model.addAttribute("patients", patientService.findAll());
            model.addAttribute("isPatient", user.isPatient());

            return "insurances/edit";
        }

        insuranceService.update(id, insurance, bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("isPatient", user.isPatient());
            model.addAttribute("patients", patientService.findAll());

            return "insurances/edit";
        }

        return "redirect:/insurances";
    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public String delete(@PathVariable Long id) {
        try {
            insuranceService.deleteById(id);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Осигуровката не съществува.");
        }

        return "redirect:/insurances";
    }
}
