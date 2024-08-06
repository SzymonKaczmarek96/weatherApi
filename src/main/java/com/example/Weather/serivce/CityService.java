package com.example.Weather.serivce;

import com.example.Weather.dto.CityDto;
import com.example.Weather.dto.CurrentWeatherDto;
import com.example.Weather.dto.HourlyWeatherDto;
import com.example.Weather.entity.*;
import com.example.Weather.exception.CityNotExistsException;
import com.example.Weather.repository.CityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class CityService {


    private WebClient webClient;
//    private final String urlForCurrentlyWeather = "https://api.openweathermap.org/data/2.5/weather";
    private final String urlForHourlyWeatherForecast = "https://api.openweathermap.org/data/2.5/forecast";

    private final String units = "metric";

    @Value("${weather.api.key}")
    private String apiKey;

    @Value("${weather.api.url}")
    private String urlTest;

    private final CityRepository cityRepository;

    @Autowired
    public CityService(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
        webClient = WebClient.builder().build();
    }

    public List<CityDto> getAllCities() {
        return cityRepository.findAll().stream().map(City::toCityDto).toList();
    }

    @Transactional
    @EventListener(ApplicationReadyEvent.class)
    public List<CityDto> retrieveCurrentForecastForAllCities() {
        List<City> allCities = cityRepository.findAll();
        List<CityDto> updatedCities = new ArrayList<>();
        for (City city : allCities) {
            CurrentWeather currentWeather = new CurrentWeather(Arrays.asList(getWeatherByCityCoordinates
                    (urlTest, city.getLatitude(), city.getLongitude())));
            city.setCurrentWeather(currentWeather);
            city.setLastUpdate();
            cityRepository.save(city).toCityDto();
            updatedCities.add(city.toCityDto());
        }
        return updatedCities;
    }

    @Transactional
    @EventListener(ApplicationReadyEvent.class)
    public List<CityDto> retrieveHourlyForecastForAllCities() {
        List<City> allCities = cityRepository.findAll();
        List<CityDto> updatedCities = new ArrayList<>();
        for (City city : allCities) {
            WeatherForecast weatherForecast = new WeatherForecast(Arrays.asList(getWeatherForecastByCityCoordinates
                    (urlForHourlyWeatherForecast, city.getLatitude(), city.getLongitude())));
            city.setHourlyCurrentWeather(weatherForecast);
            city.setLastUpdate();
            cityRepository.save(city).toCityDto();
            updatedCities.add(city.toCityDto());
        }
        return updatedCities;
    }


    @Transactional
    public CityDto updateAllForecastForChosenCity(String cityName) {
        City city = cityRepository.findCityByCityName(cityName).orElseThrow(() -> new CityNotExistsException(cityName));
        CurrentWeather currentWeather = new CurrentWeather(Arrays.asList(
                getWeatherByCityCoordinates(urlTest, city.getLatitude(), city.getLongitude())));
//        WeatherForecast weatherForecast = new WeatherForecast(Arrays.asList(
//                getWeatherForecastByCityCoordinates(urlForHourlyWeatherForecast, city.getLatitude(), city.getLongitude())));
        city.setCurrentWeather(currentWeather);
//        city.setHourlyCurrentWeather(weatherForecast);
        city.setLastUpdate();
        return cityRepository.save(city).toCityDto();
    }

    public CurrentWeatherDto displayCurrentWeather(String cityName) {
        City city = cityRepository.findCityByCityName(cityName).orElseThrow(() -> new CityNotExistsException(cityName));
        return new CurrentWeatherDto(cityName,
                city.getCurrentWeather().getCurrentWeatherList().stream().map(data -> data.getMain().getTemperature()).findFirst().get(),
                city.getCurrentWeather().getCurrentWeatherList().stream().map(data -> data.getMain().getHumidity()).findFirst().get(),
                city.getCurrentWeather().getCurrentWeatherList().stream().map(data -> data.getWind().getWindSpeed()).findFirst().get(),
                city.getLastUpdate());
    }

    public List<HourlyWeatherDto> displayHourlyWeather(String cityName) {
        City city = cityRepository.findCityByCityName(cityName).orElseThrow(() -> new CityNotExistsException(cityName));
        List<HourlyWeatherDto> hourlyWeatherDtoList = new ArrayList<>();
        List<WeatherForecastHourlyParameters> hourlyParameters = city.getHourlyCurrentWeather().getWeatherForecastHourlyParametersList().stream().map(data -> data.getList()).findFirst().get();
        for (WeatherForecastHourlyParameters parameters : hourlyParameters) {
            hourlyWeatherDtoList.add(
                    new HourlyWeatherDto(cityName, parameters.getMain().getTemperature(), parameters.getMain().getHumidity(),
                            parameters.getWind().getWindSpeed(), parameters.getDtTxt()));
        }
        return hourlyWeatherDtoList;
    }

    private CurrentWeatherParameters getWeatherByCityCoordinates(String apiUrl, double coordinateX, double coordinateY) {
        String url = String.format("%s?lat=%f&lon=%f&appid=%s&units=%s", apiUrl, coordinateX, coordinateY, apiKey,units);
       return this.webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(CurrentWeatherParameters.class)
                .block();

    }
    private WeatherForecastHourly getWeatherForecastByCityCoordinates(String apiUrl, double coordinateX, double coordinateY) {
        String url = String.format("%s?lat=%f&lon=%f&appid=%s&units=%s", apiUrl, coordinateX, coordinateY, apiKey,units);
        return this.webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(WeatherForecastHourly.class)
                .block();
    }

    }

