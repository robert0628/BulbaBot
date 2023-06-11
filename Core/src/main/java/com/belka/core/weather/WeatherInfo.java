package com.belka.core.weather;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class WeatherInfo {
    private Integer temp;

    @JsonProperty("feels_like")
    private Integer feelsLike;
}
