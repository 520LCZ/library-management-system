package com.library.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${library.covers-path:}")
    private String coversPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        if (coversPath != null && !coversPath.isEmpty()) {
            String location = coversPath.endsWith("/") ? coversPath : coversPath + "/";
            registry.addResourceHandler("/covers/**")
                    .addResourceLocations("file:" + location);
        }
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + System.getProperty("user.dir") + "/uploads/");
    }
}
