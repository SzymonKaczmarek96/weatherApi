package com.example.Weather.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class CurrentWeather implements Serializable {
    @JsonProperty("currentWeatherList")
    private List<CurrentWeatherParameters> currentWeatherList = new ArrayList<>();
}
