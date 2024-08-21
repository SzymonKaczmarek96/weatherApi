package com.example.Weather.serivce;

import com.example.Weather.entity.CurrentWeatherParameters;
import com.example.Weather.entity.WeatherForecastHourly;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class WeatherApiService {
    private final String UNITS = "metric";
    @Value("${weather.api.key}")
    private String apiKey;
    @Value("${weather.api.url}")
    private String urlWeather;
    private WebClient webClient;

    @Autowired
    public WeatherApiService() {
        this.webClient = WebClient.builder().build();
    }

    public CurrentWeatherParameters getCurrentWeatherByCityCoordinates(double latitude, double longitude) {
        String url = String.format("%s/weather?lat=%f&lon=%f&appid=%s&units=%s", urlWeather, latitude, longitude, apiKey, UNITS);
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(CurrentWeatherParameters.class)
                .block();
    }

    public WeatherForecastHourly getWeatherForecastByCityCoordinates(double latitude, double longitude) {
        String url = String.format("%s/forecast?lat=%f&lon=%f&appid=%s&units=%s", urlWeather, latitude, longitude, apiKey, UNITS);
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(WeatherForecastHourly.class)
                .block();

    }

}
