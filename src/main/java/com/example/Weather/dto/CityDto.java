package com.example.Weather.dto;

import com.example.Weather.entity.CurrentWeather;
import com.example.Weather.entity.WeatherForecast;

import java.time.LocalDateTime;

public record CityDto(String cityName, double coordinateX, double coordinateY, CurrentWeather currentWeather,
                      WeatherForecast hourlyCurrentWeather, LocalDateTime lastUpdate) {
}
