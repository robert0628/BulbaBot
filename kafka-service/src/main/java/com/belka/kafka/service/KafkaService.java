package com.belka.kafka.service;

import com.belka.core.weather_core.model.WeatherHistory;
import com.belka.core.weather_core.model.weather.WeatherNow;
import com.belka.kafka.producer.KafkaProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Slf4j
public class KafkaService {
    private final static String CRON_EVERY_MINUTE = "1 * * * * *";
    private final RestTemplate restTemplate;
    private final KafkaProducer producer;
    private final Collection<String> cities;
    private final ExecutorService executorService;
    @Value("${weather.link}")
    private String link;

    @Autowired
    public KafkaService(RestTemplate restTemplate, KafkaProducer producer,
                        @Value("${cities.list:}#{T(java.util.Collections).emptyList()}") Collection<String> cities) {
        this.restTemplate = restTemplate;
        this.producer = producer;
        this.cities = cities;
        executorService = Executors.newFixedThreadPool(cities.size());
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
        for (String city : cities) {
            executorService.execute(() -> {
                WeatherNow weatherNow = getWeather(city);
                WeatherHistory weatherHistory = WeatherHistory.builder()
                        .temp(weatherNow.getWeatherInfo().getTemp())
                        .city(city)
                        .date(LocalDateTime.now())
                        .build();
                producer.sendMessage(weatherHistory);
                log.info("we saved it");
            });
        }
    }
}
