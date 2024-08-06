package com.example.Weather.config;

import com.example.Weather.serivce.CityService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
    @Component
    public class CurrentlyWeatherJob implements Job {
        @Autowired
        private CityService cityService;

        public void execute(JobExecutionContext context) throws JobExecutionException {
            cityService.retrieveCurrentForecastForAllCities();
        }
    }
