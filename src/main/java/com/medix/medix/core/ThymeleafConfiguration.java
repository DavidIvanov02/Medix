package com.medix.medix.core;

import com.medix.medix.utils.ThymeleafBinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;
import lombok.RequiredArgsConstructor;

import java.util.Collections;

@Configuration
@RequiredArgsConstructor
public class ThymeleafConfiguration implements WebMvcConfigurer {
    private final ThymeleafBinder thymeleafBinder;
    private final ThymeleafViewResolver thymeleafViewResolver;

    @Autowired
    public void configureViewResolver(SpringTemplateEngine templateEngine) {
        thymeleafViewResolver.setStaticVariables(Collections.singletonMap("utils", thymeleafBinder));
    }
}