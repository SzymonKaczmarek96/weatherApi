package com.example.Weather.config;

import org.quartz.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfig {

    @Bean
    @Qualifier("currentWeatherJobDetail")
    public JobDetail currentWeatherJobDetails(){
        return JobBuilder.newJob(CurrentlyWeatherJob.class)
                .withIdentity("currentlyWeatherJob")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger currentWeatherTrigger(@Qualifier("currentWeatherJobDetail") JobDetail jobDetail) {
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity("currentWeatherTrigger")
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInMinutes(1)
                        .repeatForever())
                .build();
    }

    @Bean
    @Qualifier("hourlyForecastWeatherJobDetails")
    public JobDetail hourlyForecastWeatherJobDetails(){
        return JobBuilder.newJob(HourlyWeatherForecastJob.class)
                .withIdentity("hourlyWeatherForecastJob")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger hourlyForecastWeatherTrigger(@Qualifier("hourlyForecastWeatherJobDetails") JobDetail jobDetail){
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity("hourlyForecastWeatherJobDetails")
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInMinutes(5)
                        .repeatForever())
                .build();
    }


}
