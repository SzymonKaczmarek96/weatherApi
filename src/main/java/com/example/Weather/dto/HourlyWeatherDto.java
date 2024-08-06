package com.example.Weather.dto;

public record HourlyWeatherDto(String cityName, double celsiusTemp, int humidityPercentage, double windSpeed, String dataTime) {
}
