package com.donaton.bff.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient donacionesClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:8081") // Puerto de Donaciones
                .build();
    }

    @Bean
    public WebClient logisticaClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:8082") // Puerto de Logística
                .build();
    }

    @Bean
    public WebClient necesidadesClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:8083") // Puerto de Necesidades
                .build();
    }
}