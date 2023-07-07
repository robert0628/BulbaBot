package com.belka.weather.converter;

import com.belka.core.converter.BelkaConverter;
import com.belka.weather.dto.WeatherHistoryDto;
import com.belka.weather.entity.WeatherHistoryEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class JsonWeatherHistoryToWeatherHistoryEntityConverter implements BelkaConverter<WeatherHistoryDto, WeatherHistoryEntity> {
    @Override
    public WeatherHistoryEntity convert(WeatherHistoryDto value) {
        int[] inputDate = value.getDate();
        LocalDateTime date = LocalDateTime.of(
                inputDate[0], inputDate[1], inputDate[2], inputDate[3], inputDate[4], inputDate[5], inputDate[6]
        );
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
