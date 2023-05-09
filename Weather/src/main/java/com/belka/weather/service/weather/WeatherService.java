package com.belka.weather.service.weather;

/**
 * get weather for user
 */
public interface WeatherService {
    /**
     * get response for user about weather in his city
     * @param city user's city
     */
    String getWeatherResponse(String city);

    /**
     * get user's city
     */
    String findCity();
}
