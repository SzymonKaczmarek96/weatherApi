package com.example.Weather.entity;

import com.example.Weather.dto.CurrentWeatherDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class CurrentWeather implements Serializable {

    @JsonProperty("currentWeatherList")
    private List<CurrentWeatherParameters> currentWeatherList = new ArrayList<>();

    public CurrentWeatherDto toCurrentWeatherDto(String cityName) {
        return new CurrentWeatherDto(cityName,
                getCurrentWeatherList().stream().map(data -> data.getMain().getTemperature()).findFirst().get(),
                getCurrentWeatherList().stream().map(data -> data.getMain().getHumidity()).findFirst().get(),
                getCurrentWeatherList().stream().map(data -> data.getWind().getWindSpeed()).findFirst().get(),
                LocalDateTime.now());
    }
}
