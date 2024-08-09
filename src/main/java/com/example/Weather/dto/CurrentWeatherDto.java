package com.example.Weather.dto;

import java.time.LocalDateTime;

public record CurrentWeatherDto(String cityName, double celsiusTemp, int humidityPercentage, double speedWind,
                                LocalDateTime dataTime) {
}
