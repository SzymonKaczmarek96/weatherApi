package com.example.Weather.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class WeatherForecastHourlyParameters implements Serializable {

    @JsonProperty("main")
    private TemperatureParameters main;

    @JsonProperty("wind")
    private WindParameters wind;

    @JsonProperty("dt_txt")
    private String dateTime;

}
