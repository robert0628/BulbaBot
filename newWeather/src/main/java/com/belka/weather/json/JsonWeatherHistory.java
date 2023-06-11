package com.belka.weather.json;

import lombok.Data;
@Data
public class JsonWeatherHistory {
    private Long id;
    private Integer temp;
    private int[] date;
    private String city;
}
