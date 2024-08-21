package com.example.Weather.config;

import com.example.Weather.serivce.CityService;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class HourlyWeatherForecastJob implements Job {

    private CityService cityService;

    public HourlyWeatherForecastJob(CityService cityService) {
        this.cityService = cityService;
    }

    public void execute(JobExecutionContext context) {
        cityService.getWeatherForecastForAllCities();
    }
}




