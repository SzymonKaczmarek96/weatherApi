package com.example.Weather.serivce;

import com.example.Weather.dto.CityDto;
import com.example.Weather.dto.CurrentWeatherDto;
import com.example.Weather.dto.WeatherForecastDto;
import com.example.Weather.entity.City;
import com.example.Weather.entity.CurrentWeather;
import com.example.Weather.entity.WeatherForecast;
import com.example.Weather.entity.WeatherForecastHourlyParameters;
import com.example.Weather.exception.CityNotExistsException;
import com.example.Weather.repository.CityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class CityService {

    private final CityRepository cityRepository;
    private WeatherApiService weatherApiService;

    @Autowired
    public CityService(CityRepository cityRepository, WeatherApiService weatherApiService) {
        this.cityRepository = cityRepository;
        this.weatherApiService = weatherApiService;
    }

    public List<CityDto> getAllCities() {
        return cityRepository.findAll().stream().map(City::toCityDto).toList();
    }

    @Transactional
    @EventListener(ApplicationReadyEvent.class)
    public List<CityDto> getCurrentWeatherForAllCities() {
        List<City> allCities = cityRepository.findAll();
        List<CityDto> updatedCities = new ArrayList<>();
        for (City city : allCities) {
            CurrentWeather currentWeather = fetchCurrentWeather(city);
            city.setCurrentWeather(currentWeather);
            city.setLastUpdate();
            cityRepository.save(city).toCityDto();
            updatedCities.add(city.toCityDto());
        }
        return updatedCities;
    }

    @Transactional
    @EventListener(ApplicationReadyEvent.class)
    public List<CityDto> getWeatherForecastForAllCities() {
        List<City> allCities = cityRepository.findAll();
        List<CityDto> updatedCities = new ArrayList<>();
        for (City city : allCities) {
            WeatherForecast weatherForecast = fetchWeatherForecast(city);
            city.setWeatherForecast(weatherForecast);
            city.setLastUpdate();
            cityRepository.save(city).toCityDto();
            updatedCities.add(city.toCityDto());
        }
        return updatedCities;
    }

    @Transactional
    public CurrentWeatherDto getCurrentWeather(String cityName, boolean forceUpdate) {
        if (forceUpdate) {
            return getForceUpdateCurrentWeatherForSingleCity(cityName);
        } else {
            return getUpdateCurrentWeather(cityName).currentWeather().toCurrentWeatherDto(cityName);
        }
    }

    @Transactional
    public List<WeatherForecastDto> getWeatherForecast(String cityName, boolean forceUpdate) {
        if (forceUpdate) {
            return getForceUpdateWeatherForecastForSingleCity(cityName);
        }
        return getWeatherForecastList(cityName, getUpdateForecastWeather(cityName).hourlyCurrentWeather());
    }

    private CurrentWeatherDto getForceUpdateCurrentWeatherForSingleCity(String cityName) {
        City city = cityRepository.findCityByCityName(cityName).orElseThrow(() -> new CityNotExistsException(cityName));
        CurrentWeather currentWeather = fetchCurrentWeather(city);
        return currentWeather.toCurrentWeatherDto(cityName);
    }

    private List<WeatherForecastDto> getForceUpdateWeatherForecastForSingleCity(String cityName) {
        City city = cityRepository.findCityByCityName(cityName).orElseThrow(() -> new CityNotExistsException(cityName));
        WeatherForecast weatherForecast = fetchWeatherForecast(city);
        return getWeatherForecastList(cityName, weatherForecast);
    }

    @Transactional
    private List<WeatherForecastDto> getWeatherForecastList(String cityName, WeatherForecast weatherForecast) {
        List<WeatherForecastDto> forecastDtoList = new ArrayList<>();
        List<WeatherForecastHourlyParameters> forecastHourlyParameters = weatherForecast.getWeatherForecastHourlyParametersList()
                .stream().map(data -> data.getWeatherForecastHourlyParametersList()).findFirst().get();
        for (WeatherForecastHourlyParameters parameters : forecastHourlyParameters) {
            forecastDtoList.add(new WeatherForecastDto(cityName, parameters.getMain().getTemperature(),
                    parameters.getMain().getHumidity(), parameters.getWind().getWindSpeed(), parameters.getDateTime()));
        }
        return forecastDtoList;
    }

    @Transactional
    private CityDto getUpdateCurrentWeather(String cityName) {
        City city = cityRepository.findCityByCityName(cityName).orElseThrow(() -> new CityNotExistsException(cityName));
        CurrentWeather currentWeather = fetchCurrentWeather(city);
        city.setCurrentWeather(currentWeather);
        city.setLastUpdate();
        return cityRepository.save(city).toCityDto();
    }

    @Transactional
    private CityDto getUpdateForecastWeather(String cityName) {
        City city = cityRepository.findCityByCityName(cityName).orElseThrow(() -> new CityNotExistsException(cityName));
        WeatherForecast weatherForecast = fetchWeatherForecast(city);
        city.setWeatherForecast(weatherForecast);
        return cityRepository.save(city).toCityDto();
    }

    private CurrentWeather fetchCurrentWeather(City city) {
        return new CurrentWeather(Arrays.asList(
                weatherApiService.getCurrentWeatherByCityCoordinates(city.getLatitude(), city.getLongitude())));
    }

    private WeatherForecast fetchWeatherForecast(City city) {
        return new WeatherForecast(Arrays.asList(
                weatherApiService.getWeatherForecastByCityCoordinates(city.getLatitude(), city.getLongitude())));
    }
}

