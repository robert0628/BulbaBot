package com.belka.core.weather_core.weather;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class WeatherNow {
    private List<Weather> weather;
    @JsonProperty("main")
    private WeatherInfo weatherInfo;
}

