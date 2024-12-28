package com.medix.medix.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class DoctorControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testCreateDoctorBindingErrors() throws Exception {
        mockMvc.perform(post("/doctors/create")
                        .with(csrf())
                        .param("username", "")
                        .param("password", "password")
                        .param("isGeneralPractitioner", "true"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("doctor", "username"))
                .andExpect(view().name("doctors/create"));
    }
}