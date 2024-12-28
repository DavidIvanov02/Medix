package com.medix.medix.controllers;

import com.medix.medix.entities.User;
import com.medix.medix.services.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

@RequestMapping("/users")
@RequiredArgsConstructor
@Controller
public class UserController {
    private final UserServiceImpl userServiceImpl;

    @GetMapping
    public String index(Model model, Pageable pageable) {
        model.addAttribute("users", userServiceImpl.findAllBasedOnRole(pageable));

        return "users/index";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("!hasRole('PATIENT') and (hasRole('ADMIN') or #id == authentication.principal.id)")
    public String edit(@PathVariable Long id) {
        User user;

        try {
            user = userServiceImpl.findById(id);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Потребителят не съществува.");
        }

        if (user.isDoctor()) {
            return "redirect:/doctors/edit/" + id;
        }

        if (user.isPatient()) {
            return "redirect:/patients/edit/" + id;
        }

        if (user.isAdmin()) {
            return "redirect:/admins/edit/" + id;
        }

        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Потребителят не съществува.");
    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String delete(@PathVariable Long id) {
        try {
            userServiceImpl.deleteById(id);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Потребителят не съществува.");
        }

        return "redirect:/users";
    }
}
