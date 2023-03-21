package com.belka.weather.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
//@AllArgsConstructor
@Table(name = "weather_history")
@Entity
public class WeatherHistory {
    @Id
    private Long id;
    private Integer temp;
    private LocalDateTime date;
    private String city;

    public WeatherHistory(Integer temp, LocalDateTime date, String city) {
        this.temp = temp;
        this.date = date;
        this.city = city;
    }

    public WeatherHistory(Long id, Integer temp, LocalDateTime date, String city) {
        this.id = id;
        this.temp = temp;
        this.date = date;
        this.city = city;
    }

    /*  public static Builder builder() {
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
    }*/
}
