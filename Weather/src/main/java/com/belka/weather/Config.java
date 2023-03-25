package com.belka.weather;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.client.RestTemplate;

@Configuration
@EntityScan("com.belka.weather.model")
@EnableJpaRepositories("com.belka.weather.repository")
public class Config {
    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

}
