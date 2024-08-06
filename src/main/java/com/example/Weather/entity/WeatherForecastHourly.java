package com.example.Weather.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class WeatherForecastHourly implements Serializable {

    @JsonProperty("list")
    private List<WeatherForecastHourlyParameters> list;

}
