package com.belka.wearther.service;

import com.belka.wearther.models.WeatherNow;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

//todo add choosing city
@Service
@Slf4j
public class WeatherServiceImpl implements WeatherService {
    private final String link = "https://api.openweathermap.org/data/2.5/weather?q=%s&appid=3eeb478c0c035168d643da8d2147d871&units=metric&lang=ru";
    private final RestTemplate restTemplate;

    public WeatherServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getWeather(String city) {
        WeatherNow weatherNow = restTemplate.getForObject(getUriFromLink(getWeatherLink(city)), WeatherNow.class);

        assert weatherNow != null;
        if (weatherNow.getMain() == null)
            throw new RuntimeException("Bed request, try better");
        return "temp is " + weatherNow.getMain().getTemp() + ", filling like " + weatherNow.getMain().getFeelsLike();

    }

    private String getWeatherLink(String city) {
        return String.format(link, city);
    }

    public String findCity() {
       return "minsk";
    }

    private URI getUriFromLink(String link){
        try {
            return new URI(link);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
