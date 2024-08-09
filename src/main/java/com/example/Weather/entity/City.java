package com.example.Weather.entity;


import com.example.Weather.dto.CityDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "city")
public class City implements Serializable {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "city_id")
    private Long cityId;

    @Column(name = "city_name", unique = true, nullable = false)
    private String cityName;

    @Column(name = "latitude", unique = true, nullable = false)
    private double latitude;

    @Column(name = "longitude", unique = true, nullable = false)
    private double longitude;

    @Setter
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "current_weather")
    @JsonProperty("currentWeather")
    private CurrentWeather currentWeather;

    @Setter
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "hourly_forecast")
    @JsonProperty("hourlyCurrentWeather")
    private WeatherForecast hourlyCurrentWeather;

    @Column(name = "last_update")
    private LocalDateTime lastUpdate;

    public CityDto toCityDto() {
        return new CityDto(cityName, latitude, longitude, currentWeather, hourlyCurrentWeather, lastUpdate);
    }

    public void setLastUpdate() {
        this.lastUpdate = LocalDateTime.now();
    }
}
