package com.example.Weather.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class TemperatureParameters implements Serializable {

    @JsonProperty("temp")
    private double temperature;

    @JsonProperty("humidity")
    private int humidity;

}
