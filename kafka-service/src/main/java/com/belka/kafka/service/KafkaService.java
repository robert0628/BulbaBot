package com.belka.kafka.service;

import com.belka.core.weather_core.model.WeatherHistory;
import com.belka.core.weather_core.weather.WeatherNow;
import com.belka.kafka.producer.KafkaProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;

@org.springframework.stereotype.Service
@Slf4j
public class KafkaService {
    private final static String CRON_EVERY_MINUTE = "1 * * * * *";
    private final RestTemplate restTemplate;
    private final KafkaProducer producer;

    @Value("${weather.link}")
    private String link;

    @Autowired
    public KafkaService(RestTemplate restTemplate, KafkaProducer producer) {
        this.restTemplate = restTemplate;
        this.producer = producer;
    }

    private String getWeatherLink(String city) {
        return String.format(link, city);
    }

    private WeatherNow getWeather(String city) {
        WeatherNow weatherNow = restTemplate.getForObject(getWeatherLink(city), WeatherNow.class);
        if (weatherNow == null || weatherNow.getWeatherInfo() == null) {
            throw new RuntimeException("couldn't get weather data from remote server");
        }
        return weatherNow;
    }

    @Scheduled(cron = CRON_EVERY_MINUTE)
    private void saveWeatherEveryMinute() {
        WeatherNow weatherNow = getWeather("Minsk");
        WeatherHistory weatherHistory = WeatherHistory.builder()
                .temp(weatherNow.getWeatherInfo().getTemp())
                .city("Minsk")
                .date(LocalDate.now())
                .build();
        producer.sendMessage(weatherHistory);
        log.info("we saved it");
    }
}
