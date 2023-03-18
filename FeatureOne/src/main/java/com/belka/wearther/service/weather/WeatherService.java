package com.belka.wearther.service.weather;

/**
 * get weather for user
 */
public interface WeatherService {
    /**
     * get response for user about weather in his city
     * @param city user's city
     */
    String getWeather(String city);

    /**
     * get user's city
     */
    String findCity();
}
