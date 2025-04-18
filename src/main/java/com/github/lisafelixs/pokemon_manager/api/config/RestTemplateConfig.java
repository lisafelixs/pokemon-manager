package com.github.lisafelixs.pokemon_manager.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate RestTemplateConfig() {
        return new RestTemplate();
    }
}
