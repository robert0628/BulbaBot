package com.belka.stats;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan("com.belka.stats")
@EnableJpaRepositories("com.belka.stats")
@Getter
public class StatsConfig {
    @Value("${bot.owner}")
    private Long botOwner;
}
