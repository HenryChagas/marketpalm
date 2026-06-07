package com.marketpalm.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Mapeia a pasta local "uploads/imagens/" para ser acessível via URL "/uploads/imagens/".
     * Assim uma imagem salva em uploads/imagens/foto.jpg fica disponível em
     * http://localhost:8080/uploads/imagens/foto.jpg
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
}