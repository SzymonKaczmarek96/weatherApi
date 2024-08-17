package com.example.Weather.controller;

import com.example.Weather.dto.CityDto;
import com.example.Weather.dto.CurrentWeatherDto;
import com.example.Weather.dto.WeatherForecastDto;
import com.example.Weather.serivce.CityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/city/weather")
public class CityController {


    private CityService cityService;

    @Autowired
    public CityController(CityService cityService) {
        this.cityService = cityService;
    }

    @GetMapping
    public ResponseEntity<List<CityDto>> getCityList() {
        return ResponseEntity.ok(cityService.getAllCities());
    }

    @GetMapping("/{cityName}/current")
    public ResponseEntity<CurrentWeatherDto> getCurrentWeatherForChosenCity
            (@PathVariable String cityName,
             @RequestParam(name = "forceUpdate", required = false, defaultValue = "false") boolean forceUpdate) {
        return ResponseEntity.ok(cityService.getCurrentWeather(cityName, forceUpdate));
    }

    @GetMapping("{cityName}/forecast")
    public ResponseEntity<List<WeatherForecastDto>> getForecastWeatherForChosenCity
            (@PathVariable String cityName, @RequestParam(name = "forceUpdate", required = false, defaultValue = "false") boolean forceUpdate) {
        return ResponseEntity.ok(cityService.getWeatherForecast(cityName, forceUpdate));
    }

    @GetMapping("/current")
    public ResponseEntity<List<CityDto>> getCurrentWeatherForAllCities() {
        return ResponseEntity.ok(cityService.getCurrentWeatherForAllCities());
    }

    @GetMapping("/hourly")
    public ResponseEntity<List<CityDto>> getWeatherForecastForAllCities() {
        return ResponseEntity.ok(cityService.getWeatherForecastForAllCities());
    }

}
