package com.medix.medix.controllers;

import com.medix.medix.dtos.drug.CreateDrugRequestDto;
import com.medix.medix.dtos.drug.EditDrugRequestDto;
import com.medix.medix.entities.Drug;
import com.medix.medix.services.DrugService;
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

@RequestMapping("/drugs")
@Controller
@RequiredArgsConstructor
public class DrugController {
    private final DrugService drugService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String index(Model model, Pageable pageable) {
        model.addAttribute("drugs", drugService.findAll(pageable));

        return "drugs/index";
    }

    @GetMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public String create(Model model) {
        CreateDrugRequestDto drug = new CreateDrugRequestDto();
        model.addAttribute("drug", drug);

        return "drugs/create";
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public String store(@Valid @ModelAttribute("drug") CreateDrugRequestDto drug, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "drugs/create";
        }

        drugService.create(drug, bindingResult);

        if (bindingResult.hasErrors()) {
            return "drugs/create";
        }

        return "redirect:/drugs";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String edit(@PathVariable Long id, Model model) {
        try {
            Drug drug = drugService.findById(id);
            model.addAttribute("drug", new EditDrugRequestDto(drug));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Лекарството не съществува.");
        }

        return "drugs/edit";
    }

    @PostMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String update(@PathVariable Long id, @Valid @ModelAttribute("drug") EditDrugRequestDto drug, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "drugs/edit";
        }

        drugService.update(id, drug, bindingResult);

        if (bindingResult.hasErrors()) {
            return "drugs/edit";
        }

        return "redirect:/drugs";
    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String delete(@PathVariable Long id) {
        try {
            drugService.deleteById(id);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Лекарството не съществува.");
        }

        return "redirect:/drugs";
    }
}
