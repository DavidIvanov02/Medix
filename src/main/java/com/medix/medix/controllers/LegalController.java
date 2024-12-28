package com.medix.medix.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LegalController {
    @GetMapping("/terms-of-service")
    public String getTermsOfService() {
        return "legal/terms-of-service/index";
    }

    @GetMapping("/privacy-policy")
    public String getPrivacyPolicy() {
        return "legal/privacy-policy/index";
    }
}
