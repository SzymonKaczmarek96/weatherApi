package com.example.Weather.dto;

import com.example.Weather.entity.CurrentWeather;
import com.example.Weather.entity.WeatherForecast;

import java.io.Serializable;
import java.time.LocalDateTime;

public record CityDto(String cityName, double latitude, double longitude, CurrentWeather currentWeather,
                      WeatherForecast hourlyCurrentWeather, LocalDateTime lastUpdate) implements Serializable {
}