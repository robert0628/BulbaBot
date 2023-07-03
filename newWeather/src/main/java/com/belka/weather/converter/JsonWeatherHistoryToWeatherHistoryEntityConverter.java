package com.belka.weather.converter;

import com.belka.core.converter.BelkaConverter;
import com.belka.weather.entity.WeatherHistoryEntity;
import com.belka.weather.json.WeatherHistoryDto;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class JsonWeatherHistoryToWeatherHistoryEntityConverter implements BelkaConverter<WeatherHistoryDto, WeatherHistoryEntity> {
    @Override
    public WeatherHistoryEntity convert(WeatherHistoryDto value) {
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
    public Class<WeatherHistoryDto> getInputType() {
        return WeatherHistoryDto.class;
    }
}
