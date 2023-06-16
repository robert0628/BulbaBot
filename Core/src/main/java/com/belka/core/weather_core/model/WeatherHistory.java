package com.belka.core.weather_core.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
//todo do we need to have the weather models in the Core module?
@Getter
@Setter
@NoArgsConstructor
public class WeatherHistory {
    private Long id;
    private Integer temp;
    private LocalDate date;
    private String city;

    public WeatherHistory(Integer temp, LocalDate date, String city) {
        this.temp = temp;
        this.date = date;
        this.city = city;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Integer temp;
        private LocalDate date;
        private String city;

        public Builder temp(Integer temp) {
            this.temp = temp;
            return this;
        }

        public Builder date(LocalDate date) {
            this.date = date;
            return this;
        }

        public Builder city(String city) {
            this.city = city;
            return this;
        }

        public WeatherHistory build() {
            return new WeatherHistory(temp, date, city);
        }
    }
}
