package com.example.Weather.dto;

public record WeatherForecastDto(String cityName, double celsiusTemp, int humidityPercentage, double windSpeed,
                                 String dataTime) {
}
