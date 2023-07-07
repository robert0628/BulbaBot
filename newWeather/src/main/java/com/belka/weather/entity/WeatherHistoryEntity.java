package com.belka.weather.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Table(name = "weather_history")
@Entity
public class WeatherHistoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
    private Integer temp;
    private LocalDateTime date;
    private String city;

    public WeatherHistoryEntity(Integer temp, LocalDateTime date, String city) {
        this.temp = temp;
        this.date = date;
        this.city = city;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Integer temp;
        private LocalDateTime date;
        private String city;

        public Builder temp(Integer temp) {
            this.temp = temp;
            return this;
        }

        public Builder date(LocalDateTime date) {
            this.date = date;
            return this;
        }

        public Builder city(String city) {
            this.city = city;
            return this;
        }

        public WeatherHistoryEntity build() {
            return new WeatherHistoryEntity(temp, date, city);
        }
    }
}
