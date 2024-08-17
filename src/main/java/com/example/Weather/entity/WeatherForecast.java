package com.example.Weather.entity;

import com.example.Weather.dto.WeatherForecastDto;
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
public class WeatherForecast implements Serializable {

    @JsonProperty("forecastList")
    private List<WeatherForecastHourly> weatherForecastHourlyParametersList = new ArrayList<>();

}
