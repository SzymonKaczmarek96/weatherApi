package com.example.Weather;

import com.example.Weather.controller.CityController;
import com.example.Weather.dto.CityDto;
import com.example.Weather.dto.CurrentWeatherDto;
import com.example.Weather.dto.WeatherForecastDto;
import com.example.Weather.entity.*;
import com.example.Weather.exception.CityNotExistsException;
import com.example.Weather.repository.CityRepository;
import com.example.Weather.serivce.CityService;
import com.example.Weather.serivce.WeatherApiService;
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

    @Autowired
    private WeatherApiService weatherApiService;

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        registry.add("weather.api.url", () -> "http://localhost:8081");
    }

    @Test
    public void shouldGetAllCities() {
        //given
        saveCityForRepo();
        //then
        List<CityDto> cityDtoList = cityController.getCityList().getBody();
        //then
        assertEquals(HttpStatusCode.valueOf(200), cityController.getCityList().getStatusCode());
        assertEquals(1, cityDtoList.size());
    }


    @Test
    void shouldGetCurrentWeatherParametersForEnteredCityIfForceUpdateIsFalse() {
        //given
        saveCityForRepo();

        WireMock.stubFor(get(urlEqualTo("/weather?lat=52.150000&lon=21.020000&appid=your_api_key&units=metric"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(jsonForCurrentWeather())));
        //when
        CurrentWeatherDto currentWeatherDto = cityService.getCurrentWeather("Warsaw", false);
        //then
        assertEquals("Warsaw", currentWeatherDto.cityName());
        assertEquals(80, currentWeatherDto.humidityPercentage());
        assertEquals(28.43, currentWeatherDto.celsiusTemp());
        assertEquals(1.43, currentWeatherDto.speedWind());
    }

    @Test
    void shouldGetCurrentWeatherParametersForEnteredCityIfForceUpdateIsTrue() {
        //given
        saveCityForRepo();

        WireMock.stubFor(get(urlEqualTo("/weather?lat=52.150000&lon=21.020000&appid=your_api_key&units=metric"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(jsonForCurrentWeather())));
        //when
        CurrentWeatherDto currentWeatherDto = cityService.getCurrentWeather("Warsaw", true);
        //then
        assertEquals(HttpStatusCode.valueOf(200), cityController.getCurrentWeatherForChosenCity("Warsaw", false).getStatusCode());
        assertEquals("Warsaw", currentWeatherDto.cityName());
        assertEquals(80, currentWeatherDto.humidityPercentage());
        assertEquals(28.43, currentWeatherDto.celsiusTemp());
        assertEquals(1.43, currentWeatherDto.speedWind());
    }

    @Test
    void shouldThrowCityNotExistsExceptionWhenCityIsNonExistentAndForceUpdateIsFalseForCurrentWeather() {
        //given
        WireMock.stubFor(get(urlEqualTo("/weather?lat=52.150000&lon=21.020000&appid=your_api_key&units=metric"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withHeader("Content-Type", "application/json")
                        .withBody(jsonForCurrentWeather())
                ));
        //when
        Assertions.assertThrows(CityNotExistsException.class, () -> cityService.getCurrentWeather("Moscow", false));
    }

    @Test
    void shouldThrowCityNotExistsExceptionWhenCityIsNonExistentAndForceUpdateIsTureForCurrentWeather() {
        //given
        WireMock.stubFor(get(urlEqualTo("/weather?lat=52.150000&lon=21.020000&appid=your_api_key&units=metric"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withHeader("Content-Type", "application/json")
                        .withBody(jsonForCurrentWeather())
                ));
        //when
        Assertions.assertThrows(CityNotExistsException.class, () -> cityService.getCurrentWeather("Moscow", true));
    }


    @Test
    public void shouldGetWeatherForecastParametersForEnteredCityIfForceUpdateIsTrue() {
        //given
        saveCityForRepo();
        WireMock.stubFor(get(urlEqualTo("/forecast?lat=52.150000&lon=21.020000&appid=your_api_key&units=metric"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(jsonForWeatherForecast())
                ));
        //when
        List<WeatherForecastDto> weatherForecastDto = cityService.getWeatherForecast("Warsaw", true);
        //then
        Assertions.assertEquals("Warsaw", weatherForecastDto.stream().map(cityName -> cityName.cityName()).findFirst().get());
        Assertions.assertEquals(23.93, weatherForecastDto.stream().map(data -> data.celsiusTemp()).findFirst().get());
        Assertions.assertEquals(59, weatherForecastDto.stream().map(data -> data.humidityPercentage()).findFirst().get());
        Assertions.assertEquals(1.9, weatherForecastDto.stream().map(data -> data.windSpeed()).findFirst().get());
        Assertions.assertEquals("2024-08-08 18:00:00", weatherForecastDto.stream().map(data -> data.dataTime()).findFirst().get());
    }

    @Test
    void shouldThrowCityNotExistsExceptionWhenCityIsNonExistentAndForceUpdateIsTrueForWeatherForecast() {
        //given
        WireMock.stubFor(get(urlEqualTo("/forecast?lat=52.150000&lon=21.020000&appid=your_api_key&units=metric"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(jsonForWeatherForecast())
                ));
        //when
        Assertions.assertThrows(CityNotExistsException.class, () -> cityService.getWeatherForecast("Moscow", true));
    }

    @Test
    void shouldThrowCityNotExistsExceptionWhenCityIsNonExistentAndForceUpdateIsFalseForWeatherForecast() {
        //given
        WireMock.stubFor(get(urlEqualTo("/forecast?lat=52.150000&lon=21.020000&appid=your_api_key&units=metric"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(jsonForWeatherForecast())
                ));
        //when
        Assertions.assertThrows(CityNotExistsException.class, () -> cityService.getWeatherForecast("Moscow", false));
    }


    @Test
    public void shouldGetWeatherForecastParametersForEnteredCityIfForceUpdateIsFalse() {
        //given
        saveCityForRepo();
        WireMock.stubFor(get(urlEqualTo("/forecast?lat=52.150000&lon=21.020000&appid=your_api_key&units=metric"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(jsonForWeatherForecast())
                ));
        //when
        List<WeatherForecastDto> weatherForecastDto = cityService.getWeatherForecast("Warsaw", false);
        //then
        Assertions.assertEquals("Warsaw", weatherForecastDto.stream().map(cityName -> cityName.cityName()).findFirst().get());
        Assertions.assertEquals(23.93, weatherForecastDto.stream().map(data -> data.celsiusTemp()).findFirst().get());
        Assertions.assertEquals(59, weatherForecastDto.stream().map(data -> data.humidityPercentage()).findFirst().get());
        Assertions.assertEquals(1.9, weatherForecastDto.stream().map(data -> data.windSpeed()).findFirst().get());
        Assertions.assertEquals("2024-08-08 18:00:00", weatherForecastDto.stream().map(data -> data.dataTime()).findFirst().get());
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
        List<CityDto> cityDto = cityService.getCurrentWeatherForAllCities();
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
                        .withBody(jsonForWeatherForecast())
                ));
        //when
        List<CityDto> cityDto = cityService.getWeatherForecastForAllCities();
        //then
        Assertions.assertEquals("Warsaw", cityDto.stream().map(city -> city.cityName()).findFirst().get());
        Assertions.assertEquals(23.93, cityDto.stream().map(city -> city.hourlyCurrentWeather()
                .getWeatherForecastHourlyParametersList()
                .stream().map(forecast -> forecast.getWeatherForecastHourlyParametersList()
                        .stream().map(temp -> temp.getMain().getTemperature()).findFirst().get()).findFirst().get()).findFirst().get());
        Assertions.assertEquals(59, cityDto.stream().map(city -> city.hourlyCurrentWeather()
                .getWeatherForecastHourlyParametersList()
                .stream().map(forecast -> forecast.getWeatherForecastHourlyParametersList()
                        .stream().map(hum -> hum.getMain().getHumidity()).findFirst().get()).findFirst().get()).findFirst().get());

        Assertions.assertEquals(1.9, cityDto.stream().map(city -> city.hourlyCurrentWeather()
                .getWeatherForecastHourlyParametersList()
                .stream().map(forecast -> forecast.getWeatherForecastHourlyParametersList()
                        .stream().map(wind -> wind.getWind().getWindSpeed()).findFirst().get()).findFirst().get()).findFirst().get());
        Assertions.assertEquals("2024-08-08 18:00:00", cityDto.stream()
                .map(city -> city.hourlyCurrentWeather().getWeatherForecastHourlyParametersList()
                        .stream().map(forecast -> forecast.getWeatherForecastHourlyParametersList()
                                .stream().map(dateTime -> dateTime.getDateTime()).findFirst().get()).findFirst().get()).findFirst().get())
        ;
    }

    @Test
    public void shouldConnectWithCurrentWeatherApi() {
        //given
        saveCityForRepo();
        WireMock.stubFor(get(urlEqualTo("/weather?lat=52.150000&lon=21.020000&appid=your_api_key&units=metric"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(jsonForCurrentWeather())
                ));
        //when
        CurrentWeatherParameters currentWeatherParameters = weatherApiService.getCurrentWeatherByCityCoordinates(52.15, 21.02);
        //then
        assertEquals(80, currentWeatherParameters.getMain().getHumidity());
        assertEquals(28.43, currentWeatherParameters.getMain().getTemperature());
        assertEquals(1.43, currentWeatherParameters.getWind().getWindSpeed());
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
        weatherForecastHourlyParameters.setDateTime("2024-08-04 12:00:00");
        WeatherForecastHourly weatherForecastHourly = new WeatherForecastHourly();
        weatherForecastHourly.setWeatherForecastHourlyParametersList(List.of(weatherForecastHourlyParameters));
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

    private String jsonForWeatherForecast() {
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
