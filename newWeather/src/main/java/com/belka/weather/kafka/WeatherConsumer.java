package com.belka.weather.kafka;

import com.belka.weather.service.weather.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class WeatherConsumer {
    private final WeatherService service;
    private final String TOPIC = "weather";
    private final String GROUP_ID = "myGroup";


    @Autowired
    public WeatherConsumer(WeatherService service) {
        this.service = service;
    }

    @KafkaListener(topics = TOPIC, groupId = GROUP_ID)
    public void consume(String input) {
        service.saveBatch(input);
    }
}
