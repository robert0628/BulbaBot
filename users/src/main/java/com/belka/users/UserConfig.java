package com.belka.users;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan("com.belka.users")
@EnableJpaRepositories("com.belka.users")
@Getter
public class UserConfig {
    @Value("${bot.owner}")
    private Long botOwner;
}
