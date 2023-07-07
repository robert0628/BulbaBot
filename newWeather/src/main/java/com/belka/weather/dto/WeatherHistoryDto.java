package com.belka.weather.dto;

import lombok.Data;
@Data
public class WeatherHistoryDto {
    private Long id;
    private Integer temp;
    private int[] date;
    private String city;
}
