package com.medix.medix.core;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionsHandler {
    @ExceptionHandler(AccessDeniedException.class)
    protected String handleAccessDeniedException(AccessDeniedException exception, Model model) {
        model.addAttribute("message", "Грешка в достъпа: " + exception.getMessage());

        return "/errors/errors";
    }

    @ExceptionHandler(Exception.class)
    protected String handleException(Exception exception, Model model) {
        model.addAttribute("message", exception.getMessage());

        return "/errors/errors";
    }
}
