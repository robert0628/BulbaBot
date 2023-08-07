package com.belka.weather.kafka;

import com.belka.weather.dto.WeatherHistoryDto;
import com.belka.weather.service.weather.WeatherService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;

@Component
@Slf4j
public class WeatherConsumer {
    private final WeatherService service;
    private final String TOPIC = "weather";
    private final String GROUP_ID = "myGroup";
    @Value("${cities.list:}#{T(java.util.Collections).emptyList()}")
    private Collection<String> cities;
    private final Collection<WeatherHistoryDto> inputs = new ArrayList<>();

    @Autowired
    public WeatherConsumer(WeatherService service) {
        this.service = service;
    }

    @KafkaListener(topics = TOPIC, groupId = GROUP_ID)
    public void consume(String input) {
        inputs.add(convertToWeatherHistory(input));
        log.info(String.format("Message received -> %s", input));
        if (inputs.size() > cities.size()) {
            service.saveBatch(inputs);
            inputs.clear();
        }
    }

    private WeatherHistoryDto convertToWeatherHistory(String weather) {
        ObjectMapper mapper = new ObjectMapper();
        WeatherHistoryDto weatherHistoryDto;
        try {
            weatherHistoryDto = mapper.readValue(weather, WeatherHistoryDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return weatherHistoryDto;

    }
}
