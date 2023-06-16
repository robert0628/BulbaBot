package com.belka.weather.service.weather;

import com.belka.core.converter.ConverterService;
import com.belka.core.weather_core.weather.WeatherInfo;
import com.belka.core.weather_core.weather.WeatherNow;
import com.belka.weather.entity.WeatherHistoryEntity;
import com.belka.weather.json.JsonWeatherHistory;
import com.belka.weather.repository.WeatherRepository;
import com.belka.weather.service.geo.GeoFromIPService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
@Data
public class WeatherServiceImpl implements WeatherService {

    private final static String CRON_EVERY_DAY = "1 * * * * *";
    @Value("${weather.key}")
    private String key;
    @Value("${weather.link}")
    private String link;
    private final RestTemplate restTemplate;
    private final GeoFromIPService geoFromIPService;
    private WeatherRepository repository;
    private ConverterService converterService;

    @Autowired
    public void setConverterService(ConverterService converterService) {
        this.converterService = converterService;
    }

    @Autowired
    public void setRepository(WeatherRepository repository) {
        this.repository = repository;
    }

    public String getWeatherResponse(String city) {
        WeatherInfo weatherInfo = getWeather(city).getWeatherInfo();
        String text = "temp is " + weatherInfo.getTemp() + ", filling like " + weatherInfo.getFeelsLike();
        if (weatherInfo.getTemp() < 0) {
            return "⛄" + text + "⛄";
        }
        return text;
    }

    private WeatherNow getWeather(String city) {
        WeatherNow weatherNow = restTemplate.getForObject(getWeatherLink(city), WeatherNow.class);
        if (weatherNow == null || weatherNow.getWeatherInfo() == null) {
            throw new RuntimeException("couldn't get weather data from remote server");
        }
        return weatherNow;
    }

    private String getWeatherLink(String city) {
        return String.format(link, city);
    }

    public String findCity() {
        return geoFromIPService.getCityName();
    }

    @KafkaListener(topics = "weather", groupId = "myGroup")
    public void consume(String input) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonWeatherHistory jsonWeatherHistory = mapper.readValue(input, JsonWeatherHistory.class);
            WeatherHistoryEntity entity = converterService.ConvertTo(WeatherHistoryEntity.class, jsonWeatherHistory);
            System.out.println(entity);
            repository.save(entity);
            log.info(String.format("Message received -> %s", entity));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


}
