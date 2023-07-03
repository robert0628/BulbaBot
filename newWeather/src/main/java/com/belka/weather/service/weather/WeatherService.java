package com.belka.weather.service.weather;

import com.belka.weather.json.WeatherHistoryDto;

import java.util.Collection;

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

    /**
     * save collection {@link WeatherHistoryDto} to DB
     * @param weathers collection {@link WeatherHistoryDto} for saving
     */
    void saveBatch(Collection<WeatherHistoryDto> weathers);
}
