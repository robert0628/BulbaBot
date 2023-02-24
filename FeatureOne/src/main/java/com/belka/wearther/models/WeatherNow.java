package com.belka.wearther.models;

import lombok.Data;

import java.util.List;

@Data
    public class WeatherNow {
        private List<Weather> weather;
        private Main main;
    }

