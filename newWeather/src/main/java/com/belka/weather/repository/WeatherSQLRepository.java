package com.belka.weather.repository;

import com.belka.weather.entity.WeatherHistoryEntity;

import java.util.Collection;

public interface WeatherSQLRepository {
    void batchSave(Collection<WeatherHistoryEntity> entities);
}
