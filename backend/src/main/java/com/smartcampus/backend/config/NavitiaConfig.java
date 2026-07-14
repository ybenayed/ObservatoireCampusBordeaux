package com.smartcampus.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.web.client.RestTemplate;

@Configuration
public class NavitiaConfig {

    @Value("${navitia.token}")
    private String navitiaToken;

    @Bean(name = "navitiaRestTemplate")
    public RestTemplate navitiaRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        
        // Ajoute automatiquement l'en-tête "Authorization: Basic <token_encodé>"
        restTemplate.getInterceptors().add(
            new BasicAuthenticationInterceptor(navitiaToken, "")
        );
        
        return restTemplate;
    }
}