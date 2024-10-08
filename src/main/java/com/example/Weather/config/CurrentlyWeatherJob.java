package com.example.Weather.config;

import com.example.Weather.serivce.CityService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

@Component
public class CurrentlyWeatherJob implements Job {
    private CityService cityService;

    public CurrentlyWeatherJob(CityService cityService) {
        this.cityService = cityService;
    }

    public void execute(JobExecutionContext context) {
        cityService.getCurrentWeatherForAllCities();
    }
}
