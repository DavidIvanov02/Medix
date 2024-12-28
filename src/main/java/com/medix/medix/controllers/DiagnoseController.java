package com.medix.medix.controllers;

import com.medix.medix.dtos.diagnose.CreateDiagnoseRequestDto;
import com.medix.medix.dtos.diagnose.EditDiagnoseRequestDto;
import com.medix.medix.entities.Diagnose;
import com.medix.medix.services.DiagnoseService;
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

@RequestMapping("/diagnoses")
@Controller
@RequiredArgsConstructor
public class DiagnoseController {
    private final DiagnoseService diagnoseService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String index(Model model, Pageable pageable) {
        model.addAttribute("diagnoses", diagnoseService.findAll(pageable));

        return "diagnoses/index";
    }

    @GetMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public String create(Model model) {
        CreateDiagnoseRequestDto diagnose = new CreateDiagnoseRequestDto();
        model.addAttribute("diagnose", diagnose);

        return "diagnoses/create";
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public String store(@Valid @ModelAttribute("diagnose") CreateDiagnoseRequestDto diagnose, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "diagnoses/create";
        }

        diagnoseService.create(diagnose, bindingResult);

        if (bindingResult.hasErrors()) {
            return "diagnoses/create";
        }

        return "redirect:/diagnoses";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String edit(@PathVariable Long id, Model model) {
        try {
            Diagnose diagnose = diagnoseService.findById(id);
            model.addAttribute("diagnose", new EditDiagnoseRequestDto(diagnose));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Специалността не съществува.");
        }

        return "diagnoses/edit";
    }

    @PostMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String update(@PathVariable Long id, @Valid @ModelAttribute("diagnose") EditDiagnoseRequestDto diagnose, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "diagnoses/edit";
        }

        diagnoseService.update(id, diagnose, bindingResult);

        if (bindingResult.hasErrors()) {
            return "diagnoses/edit";
        }

        return "redirect:/diagnoses";
    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String delete(@PathVariable Long id) {
        try {
            diagnoseService.deleteById(id);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Специалността не съществува.");
        }

        return "redirect:/diagnoses";
    }

    @GetMapping("/most_seen")
    public String mostSeen(Model model) {
        model.addAttribute("diagnosesWithCount", diagnoseService.findMostSeen());

        return "diagnoses/most_seen";
    }
}
