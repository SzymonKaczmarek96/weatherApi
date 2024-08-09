package com.example.Weather;

import com.example.Weather.controller.CityController;
import com.example.Weather.dto.CityDto;
import com.example.Weather.dto.CurrentWeatherDto;
import com.example.Weather.dto.HourlyWeatherDto;
import com.example.Weather.entity.*;
import com.example.Weather.exception.CityNotExistsException;
import com.example.Weather.repository.CityRepository;
import com.example.Weather.serivce.CityService;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatusCode;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.time.LocalDateTime;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
@WireMockTest(httpPort = 8081)
class WeatherServiceTest extends TestContainer {

    @Autowired
    private CityService cityService;

    @Autowired
    private CityController cityController;

    @Autowired
    private CityRepository cityRepository;

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        registry.add("weather.api.url", () -> "http://localhost:8081");
    }

    @Test
    public void shouldGetAllCities() {
        //given
        saveCityForRepo();
        //then
        List<CityDto> cityDtoList = cityController.displayCurrentWeatherForAllCities().getBody();
        //then
        assertEquals(HttpStatusCode.valueOf(200), cityController.displayCurrentWeatherForAllCities().getStatusCode());
        assertEquals(1, cityDtoList.size());
    }


    @Test
    void shouldDisplayWeatherParametersForEnteredCity() {
        //given
        saveCityForRepo();
        //when
        CurrentWeatherDto currentWeatherDto = cityController.displayCurrentWeather("Warsaw").getBody();
        //then
        assertEquals(HttpStatusCode.valueOf(200), cityController.displayCurrentWeather("Warsaw").getStatusCode());
        assertEquals("Warsaw", currentWeatherDto.cityName());
        assertEquals(70, currentWeatherDto.humidityPercentage());
        assertEquals(25.3, currentWeatherDto.celsiusTemp());
        assertEquals(1.54, currentWeatherDto.speedWind());
    }


    @Test
    public void shouldThrowNotExistExceptionWhenCityNotExists() {
        Assertions.assertThrows(CityNotExistsException.class, () -> cityController.displayCurrentWeather("Moscow").getBody());
    }

    @Test
    public void shouldDisplayWeatherForecastForEnteredCity() {
        //given
        saveCityForRepo();
        //when
        List<HourlyWeatherDto> hourlyWeatherDto = cityController.displayHourlyWeather("Warsaw").getBody();
        //then
        assertEquals(HttpStatusCode.valueOf(200), cityController.displayHourlyWeather("Warsaw").getStatusCode());
        assertEquals(28, hourlyWeatherDto.stream().map(HourlyWeatherDto::celsiusTemp).findFirst().get());
        assertEquals(80, hourlyWeatherDto.stream().map(HourlyWeatherDto::humidityPercentage).findFirst().get());
        assertEquals(1.05, hourlyWeatherDto.stream().map(HourlyWeatherDto::windSpeed).findFirst().get());
        assertEquals("2024-08-04 12:00:00", hourlyWeatherDto.stream().map(HourlyWeatherDto::dataTime).findFirst().get());
    }


    @Test
    void shouldUpdateCurrentWeather() {
        //given
        saveCityForRepo();
        WireMock.stubFor(get(urlEqualTo("/weather?lat=52.150000&lon=21.020000&appid=your_api_key&units=metric"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(jsonForCurrentWeather())
                ));
        //when
        CityDto cityDto = cityService.updateCurrentWeatherForChosenCity("Warsaw");
        //then
        assertEquals("Warsaw", cityDto.cityName());
        assertEquals(28.43, cityDto.currentWeather().getCurrentWeatherList().get(0).getMain().getTemperature());
        assertEquals(80, cityDto.currentWeather().getCurrentWeatherList().get(0).getMain().getHumidity());
        assertEquals(1.43, cityDto.currentWeather().getCurrentWeatherList().get(0).getWind().getWindSpeed());
    }

    @Test
    void shouldThrowCityNotExistsExceptionWhenUrlIsIncorrect() {
        //given
        WireMock.stubFor(get(urlEqualTo("/weather?lat=52.150000&lon=21.020000&appid=your_api_key&units=metric"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withHeader("Content-Type", "application/json")
                        .withBody(jsonForCurrentWeather())
                ));
        //when
        Assertions.assertThrows(CityNotExistsException.class, () -> cityService.updateCurrentWeatherForChosenCity("Moscow"));
    }

    @Test
    void shouldUpdateWeatherForecast() {
        //given
        saveCityForRepo();
        WireMock.stubFor(get(urlEqualTo("/forecast?lat=52.150000&lon=21.020000&appid=your_api_key&units=metric"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(jsonForForecast())
                ));
        //when
        CityDto cityDto = cityService.updateHourlyForecastForChosenCity("Warsaw");
        //then
        Assertions.assertEquals("Warsaw", cityDto.cityName());
        Assertions.assertEquals(23.93, cityDto.hourlyCurrentWeather().getWeatherForecastHourlyParametersList().stream()
                .map(forecast -> forecast.getList().stream()
                        .map(temp -> temp.getMain().getTemperature())).findFirst().get().findFirst().get());
        Assertions.assertEquals(59, cityDto.hourlyCurrentWeather().getWeatherForecastHourlyParametersList()
                .stream().map(forecast -> forecast.getList()
                        .stream().map(hum -> hum.getMain().getHumidity()).findFirst().get()).findFirst().get());
        Assertions.assertEquals(1.9, cityDto.hourlyCurrentWeather().getWeatherForecastHourlyParametersList()
                .stream().map(forecast -> forecast.getList()
                        .stream().map(wind -> wind.getWind().getWindSpeed()).findFirst().get()).findFirst().get());
        Assertions.assertEquals("2024-08-08 18:00:00", cityDto.hourlyCurrentWeather().getWeatherForecastHourlyParametersList()
                .stream().map(forecast -> forecast.getList()
                        .stream().map(dateTime -> dateTime.getDtTxt()).findFirst().get()).findFirst().get());

    }

    @Test
    public void shouldUpdateCurrentWeatherForAllCity() {
        //given
        saveCityForRepo();
        WireMock.stubFor(get(urlEqualTo("/weather?lat=52.150000&lon=21.020000&appid=your_api_key&units=metric"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(jsonForCurrentWeather())
                ));
        //when
        List<CityDto> cityDto = cityService.retrieveCurrentForecastForAllCities();
        //then
        Assertions.assertEquals("Warsaw", cityDto.stream().map(city -> city.cityName()).findFirst().get());
        Assertions.assertEquals(28.43, cityDto.stream()
                .map(city -> city.currentWeather().getCurrentWeatherList()
                        .stream().map(temp -> temp.getMain().getTemperature()).findFirst().get()).findFirst().get());

        Assertions.assertEquals(80, cityDto.stream()
                .map(city -> city.currentWeather().getCurrentWeatherList()
                        .stream().map(hum -> hum.getMain().getHumidity()).findFirst().get()).findFirst().get());
        Assertions.assertEquals(1.43, cityDto.stream()
                .map(city -> city.currentWeather().getCurrentWeatherList()
                        .stream().map(wind -> wind.getWind().getWindSpeed()).findFirst().get()).findFirst().get());
    }

    @Test
    public void shouldUpdateWeatherForecastForAllCity() {
        //given
        saveCityForRepo();
        WireMock.stubFor(get(urlEqualTo("/forecast?lat=52.150000&lon=21.020000&appid=your_api_key&units=metric"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(jsonForForecast())
                ));
        //when
        List<CityDto> cityDto = cityService.retrieveHourlyForecastForAllCities();
        //then
        Assertions.assertEquals("Warsaw", cityDto.stream().map(city -> city.cityName()).findFirst().get());
        Assertions.assertEquals(23.93, cityDto.stream().map(city -> city.hourlyCurrentWeather()
                .getWeatherForecastHourlyParametersList()
                .stream().map(forecast -> forecast.getList()
                        .stream().map(temp -> temp.getMain().getTemperature()).findFirst().get()).findFirst().get()).findFirst().get());
        Assertions.assertEquals(59, cityDto.stream().map(city -> city.hourlyCurrentWeather()
                .getWeatherForecastHourlyParametersList()
                .stream().map(forecast -> forecast.getList()
                        .stream().map(hum -> hum.getMain().getHumidity()).findFirst().get()).findFirst().get()).findFirst().get());

        Assertions.assertEquals(1.9, cityDto.stream().map(city -> city.hourlyCurrentWeather()
                .getWeatherForecastHourlyParametersList()
                .stream().map(forecast -> forecast.getList()
                        .stream().map(wind -> wind.getWind().getWindSpeed()).findFirst().get()).findFirst().get()).findFirst().get());
        Assertions.assertEquals("2024-08-08 18:00:00", cityDto.stream()
                .map(city -> city.hourlyCurrentWeather().getWeatherForecastHourlyParametersList()
                        .stream().map(forecast -> forecast.getList()
                                .stream().map(dateTime -> dateTime.getDtTxt()).findFirst().get()).findFirst().get()).findFirst().get())
        ;
    }

    private void saveCityForRepo() {
        WeatherForecast weatherForecast = new WeatherForecast(List.of(createWeatherForecast()));
        CurrentWeather currentWeather = new CurrentWeather(List.of(createCurrentWeather()));
        City city = new City(1L, "Warsaw", 52.15, 21.02, currentWeather, weatherForecast, LocalDateTime.now());
        cityRepository.save(city);
    }

    private CurrentWeatherParameters createCurrentWeather() {
        TemperatureParameters temperatureParameters = new TemperatureParameters();
        temperatureParameters.setTemperature(25.3);
        temperatureParameters.setHumidity(70);
        WindParameters windParameters = new WindParameters();
        windParameters.setWindSpeed(1.54);
        CurrentWeatherParameters currentWeatherParameters = new CurrentWeatherParameters();
        currentWeatherParameters.setMain(temperatureParameters);
        currentWeatherParameters.setWind(windParameters);
        return currentWeatherParameters;
    }

    private WeatherForecastHourly createWeatherForecast() {
        TemperatureParameters temperatureParameters = new TemperatureParameters();
        temperatureParameters.setTemperature(28);
        temperatureParameters.setHumidity(80);
        WindParameters windParameters = new WindParameters();
        windParameters.setWindSpeed(1.05);
        WeatherForecastHourlyParameters weatherForecastHourlyParameters = new WeatherForecastHourlyParameters();
        weatherForecastHourlyParameters.setMain(temperatureParameters);
        weatherForecastHourlyParameters.setWind(windParameters);
        weatherForecastHourlyParameters.setDtTxt("2024-08-04 12:00:00");
        WeatherForecastHourly weatherForecastHourly = new WeatherForecastHourly();
        weatherForecastHourly.setList(List.of(weatherForecastHourlyParameters));
        return weatherForecastHourly;
    }

    private String jsonForCurrentWeather() {
        return "{ " +
                "\"main\": { " +
                "\"temp\": 28.43, " +
                "\"humidity\": 80 " +
                "}, " +
                "\"wind\": { " +
                "\"speed\": 1.43 " +
                "} " +
                "} ";

    }


    private String jsonForForecast() {
        return "{ " +
                "\"list\": [ { " +
                "\"main\": { " +
                "\"temp\": 23.93, " +
                "\"humidity\": 59 " +
                "}, " +
                "\"wind\": { " +
                "\"speed\": 1.9 " +
                "}, " +
                "\"dt_txt\": \"2024-08-08 18:00:00\" " +
                "} ] " +
                "} ";
    }
}
