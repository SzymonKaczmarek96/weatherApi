package com.example.Weather.config;

import org.quartz.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfig {

    @Bean
    public JobDetail currentWeatherJobDetail() {
        return JobBuilder.newJob(CurrentlyWeatherJob.class)
                .withIdentity("currentlyWeatherJob")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger currentWeatherTrigger(JobDetail currentWeatherJobDetail) {
        return TriggerBuilder.newTrigger()
                .forJob(currentWeatherJobDetail)
                .withIdentity("currentWeatherTrigger")
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInMinutes(30)
                        .repeatForever())
                .build();
    }

    @Bean
    public JobDetail hourlyForecastWeatherJobDetail() {
        return JobBuilder.newJob(HourlyWeatherForecastJob.class)
                .withIdentity("hourlyWeatherForecastJob")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger hourlyForecastWeatherTrigger(JobDetail hourlyForecastWeatherJobDetail) {
        return TriggerBuilder.newTrigger()
                .forJob(hourlyForecastWeatherJobDetail)
                .withIdentity("hourlyForecastWeatherTrigger")
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInHours(2)
                        .repeatForever())
                .build();
    }
}
