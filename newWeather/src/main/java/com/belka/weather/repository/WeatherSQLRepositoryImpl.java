package com.belka.weather.repository;

import com.belka.weather.entity.WeatherHistoryEntity;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Repository
@AllArgsConstructor
public class WeatherSQLRepositoryImpl implements WeatherSQLRepository {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final String INSERT_SQL = "INSERT INTO weather_history (city, date, temp) VALUES(:city, :date, :temp)";

    @Override
    public void batchSave(Collection<WeatherHistoryEntity> entities) {
        namedParameterJdbcTemplate.batchUpdate(INSERT_SQL, getParams(entities).toArray(MapSqlParameterSource[]::new));
    }

    private List<MapSqlParameterSource> getParams(Collection<WeatherHistoryEntity> entities) {
        List<MapSqlParameterSource> params = new ArrayList<>();
        for (WeatherHistoryEntity entity : entities) {
            MapSqlParameterSource source = new MapSqlParameterSource();
            source.addValue("city", entity.getCity());
            source.addValue("date", entity.getDate());
            source.addValue("temp", entity.getTemp());
            params.add(source);
        }
        return params;
    }

}
