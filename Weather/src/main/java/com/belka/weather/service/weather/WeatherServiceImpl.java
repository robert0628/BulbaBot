package com.belka.weather.service.weather;

import com.belka.weather.dto.weather.WeatherInfo;
import com.belka.weather.dto.weather.WeatherNow;
import com.belka.weather.model.WeatherHistory;
import com.belka.weather.repository.WeatherRepository;
import com.belka.weather.service.geo.GeoFromIPService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;

@Service
@Slf4j
@Data
@ComponentScan ("com.belka")
public class WeatherServiceImpl implements WeatherService {

    private final static String CRON_EVERY_DAY = "1 * * * * *";
    private static final String ERROR_TEXT = "Error occurred: ";
    @Value("${weather.key}")
    private String key;
    @Value("${weather.link}")
    private String link;
    @Value("${weather.city}")
    private String cityForEveryDay;
    private final RestTemplate restTemplate;
    private final GeoFromIPService geoFromIPService;
    private WeatherRepository repository;

    @Autowired
    public void setRepository( WeatherRepository repository) {
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

    @Scheduled(cron = CRON_EVERY_DAY)
    private void saveWeatherEveryDay() {
        WeatherNow weatherNow = getWeather(cityForEveryDay);
        WeatherHistory weatherHistory =
                WeatherHistory.builder()
                        .temp(weatherNow.getWeatherInfo().getTemp())
                        .city(cityForEveryDay)
                        .date(LocalDate.now())
                        .build();
        repository.save(weatherHistory);
        log.info("we saved it");
    }


}
