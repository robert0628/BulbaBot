package com.belka.weather.converter;

import com.belka.core.converter.BelConverter;
import com.belka.weather.entity.WeatherHistoryEntity;
import com.belka.weather.json.JsonWeatherHistory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class WeatherHistoryToWeatherHistoryEntityConverter implements BelConverter<JsonWeatherHistory, WeatherHistoryEntity> {
    @Override
    public WeatherHistoryEntity convert(JsonWeatherHistory value) {
        int[] inputDate = value.getDate();
        LocalDate date = LocalDate.of(inputDate[0], inputDate[1], inputDate[2]);
        return WeatherHistoryEntity.builder()
                .city(value.getCity())
                .date(date)
                .temp(value.getTemp())
                .build();
    }

    @Override
    public Class<WeatherHistoryEntity> getOutputType() {
        return WeatherHistoryEntity.class;
    }

    @Override
    public Class<JsonWeatherHistory> getInputType() {
        return JsonWeatherHistory.class;
    }
}
