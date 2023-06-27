package com.belka.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableScheduling
@EntityScan("com.belka.kafka")
@EnableJpaRepositories("com.belka.kafka")
public class KafkaConfig {

    @Value("${spring.kafka.topic.name}")
    private String topic;

    @Bean
    public NewTopic weatherTopic() {
        return TopicBuilder.name(topic)
                .build();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
