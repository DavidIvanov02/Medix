package com.medix.medix.controllers;

import com.medix.medix.dtos.speciality.EditSpecialityRequestDto;
import com.medix.medix.dtos.speciality.CreateSpecialityRequestDto;
import com.medix.medix.entities.Speciality;
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

@RequestMapping("/specialities")
@Controller
@RequiredArgsConstructor
public class SpecialityController {
    private final SpecialityService specialityService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String index(Model model, Pageable pageable) {
        model.addAttribute("specialities", specialityService.findAll(pageable));

        return "specialities/index";
    }

    @GetMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public String create(Model model) {
        CreateSpecialityRequestDto speciality = new CreateSpecialityRequestDto();
        model.addAttribute("speciality", speciality);

        return "specialities/create";
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public String store(@Valid @ModelAttribute("speciality") CreateSpecialityRequestDto speciality, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "specialities/create";
        }

        specialityService.create(speciality, bindingResult);

        if (bindingResult.hasErrors()) {
            return "specialities/create";
        }

        return "redirect:/specialities";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String edit(@PathVariable Long id, Model model) {
        try {
            Speciality speciality = specialityService.findById(id);
            model.addAttribute("speciality", new EditSpecialityRequestDto(speciality));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Специалността не съществува.");
        }

        return "specialities/edit";
    }

    @PostMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String update(@PathVariable Long id, @Valid @ModelAttribute("speciality") EditSpecialityRequestDto speciality, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "specialities/edit";
        }

        specialityService.update(id, speciality, bindingResult);

        if (bindingResult.hasErrors()) {
            return "specialities/edit";
        }

        return "redirect:/specialities";
    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String delete(@PathVariable Long id) {
        try {
            specialityService.deleteById(id);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Специалността не съществува.");
        }

        return "redirect:/specialities";
    }
}
