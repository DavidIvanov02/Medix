package com.medix.medix.controllers;

import com.medix.medix.dtos.leave.EditLeaveRequestDto;
import com.medix.medix.dtos.leave.CreateLeaveRequestDto;
import com.medix.medix.entities.Leave;
import com.medix.medix.services.AppointmentService;
import com.medix.medix.services.LeaveService;
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

@RequestMapping("/leaves")
@Controller
@RequiredArgsConstructor
public class LeaveController {
    private final AppointmentService appointmentService;
    private final LeaveService leaveService;

    @GetMapping
    public String index(Model model, Pageable pageable) {
        model.addAttribute("leaves", leaveService.findAllBasedOnRole(pageable));

        return "leaves/index";
    }

    @GetMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public String create(Model model, @RequestParam(required = false) Long appointmentId) {
        CreateLeaveRequestDto leave = new CreateLeaveRequestDto();

        if (appointmentId != null)
        {
            leave.setAppointmentId(appointmentId);
        }

        model.addAttribute("leave", leave);
        model.addAttribute("appointments", appointmentService.findAllBasedOnRole());

        return "leaves/create";
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public String store(@Valid @ModelAttribute("leave") CreateLeaveRequestDto leave, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("appointments", appointmentService.findAllBasedOnRole());
            return "leaves/create";
        }

        leaveService.create(leave, bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("appointments", appointmentService.findAllBasedOnRole());
            return "leaves/create";
        }

        return "redirect:/leaves";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('DOCTOR') and @leaveService.isLeaveAppointmentFromDoctor(#id, principal.id))")
    public String edit(@PathVariable Long id, Model model) {
        try {
            Leave leave = leaveService.findById(id);

            model.addAttribute("leave", new EditLeaveRequestDto(leave));
            model.addAttribute("appointments", appointmentService.findAllBasedOnRole());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Болничният лист не съществува.");
        }

        return "leaves/edit";
    }

    @PostMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('DOCTOR') and @leaveService.isLeaveAppointmentFromDoctor(#id, principal.id))")
    public String update(@PathVariable Long id, @Valid @ModelAttribute("leave") EditLeaveRequestDto leave, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("appointments", appointmentService.findAllBasedOnRole());

            return "leaves/edit";
        }

        leaveService.update(id, leave, bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("appointments", appointmentService.findAllBasedOnRole());

            return "leaves/edit";
        }

        return "redirect:/leaves";
    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public String delete(@PathVariable Long id) {
        try {
            leaveService.deleteById(id);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Болничният лист не съществува.");
        }

        return "redirect:/leaves";
    }
}
