package com.belka.audio.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan("com.belka.audio")
@EnableJpaRepositories("com.belka.audio")
public class AudioConfig {
}
