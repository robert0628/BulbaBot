package com.belka.weather.service.weather;

import com.belka.core.weather.WeatherInfo;
import com.belka.core.weather.WeatherNow;
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

import java.time.LocalDate;

@Service
@Slf4j
@Data
//@ComponentScan ("com.belka")
public class WeatherServiceImpl implements WeatherService {

    private final static String CRON_EVERY_DAY = "1 * * * * *";
    @Value("${weather.key}")
    private String key;
    @Value("${weather.link}")
    private String link;
    private final RestTemplate restTemplate;
    private final GeoFromIPService geoFromIPService;
    private WeatherRepository repository;

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

    @KafkaListener(topics = "diaryNotes", groupId = "myGroup")
    public void consume(String input) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonWeatherHistory jsonWeatherHistory = mapper.readValue(input, JsonWeatherHistory.class);
            int[]inputDate = jsonWeatherHistory.getDate();
            LocalDate date = LocalDate.of(inputDate[0], inputDate[1], inputDate[2]);
            WeatherHistoryEntity entity = WeatherHistoryEntity.builder()
                    .temp(jsonWeatherHistory.getTemp())
                    .date(date)
                    .city(jsonWeatherHistory.getCity())
                    .build();

            repository.save(entity);
            log.info(String.format("Message received -> %s", entity));
    } catch(
    JsonProcessingException e)

    {
        throw new RuntimeException(e);
    }
}


}
