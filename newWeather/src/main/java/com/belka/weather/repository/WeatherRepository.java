package com.belka.weather.repository;

import com.belka.weather.model.WeatherHistory;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * repository for {@link WeatherHistory WeatherHistory}
 */
public interface WeatherRepository extends JpaRepository<WeatherHistory, Long> {
}
