package com.example.Weather.controller;

import com.example.Weather.dto.CityDto;
import com.example.Weather.dto.CurrentWeatherDto;
import com.example.Weather.dto.HourlyWeatherDto;
import com.example.Weather.serivce.CityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CityController {
    @Autowired
    private CityService cityService;

    @GetMapping
    public ResponseEntity<List<CityDto>> displayCurrentWeatherForAllCities(){
        return ResponseEntity.ok(cityService.getAllCities());
    }

    @GetMapping("/display/current/{cityName}")
    public ResponseEntity<CurrentWeatherDto> displayCurrentWeather(@PathVariable String cityName){
        return ResponseEntity.ok(cityService.displayCurrentWeather(cityName));
    }

    @GetMapping("/display/hourly/{cityName}")
    public ResponseEntity<List<HourlyWeatherDto>> displayHourlyWeather(@PathVariable String cityName){
        return ResponseEntity.ok(cityService.displayHourlyWeather(cityName));
    }

    @PutMapping("/update/{cityName}")
    public ResponseEntity<CityDto> getUpdateWeatherInformation(@PathVariable String cityName){
        return ResponseEntity.ok(cityService.updateAllForecastForChosenCity(cityName));
    }

    @PutMapping("/current/all")
    public ResponseEntity<List<CityDto>> getCurrentWeatherForAllCities(){
        return ResponseEntity.ok(cityService.retrieveCurrentForecastForAllCities());
    }

    @PutMapping("/hourly/all")
    public ResponseEntity<List<CityDto>> getHourlyForecastForAllCities(){
        return ResponseEntity.ok(cityService.retrieveHourlyForecastForAllCities());
    }

}
