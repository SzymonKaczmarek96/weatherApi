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
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.ClassRule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatusCode;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDateTime;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

//@RunWith(SpringRunner.class)
@SpringBootTest()
@WireMockTest(httpPort = 8081)
class WeatherServiceTest extends TestContainer{

	@Autowired
	private CityService cityService;

	@Autowired
	private CityController cityController;

	@Autowired
	private CityRepository cityRepository;

//	@Autowired
//	private WebTestClient webTestClient;

//	@ClassRule
//	public static WireMockClassRule wireMockClassRule
//			= new WireMockClassRule(WireMockConfiguration.wireMockConfig().dynamicPort());

	@DynamicPropertySource
	static void configure(DynamicPropertyRegistry registry) {
		registry.add("weather.api.url",() ->"http://localhost:8081/weather");
	}

//	@BeforeEach
//	public void setup() {
//		wireMockClassRule.resetAll();
//	}

	@Test
	public void shouldGetAllCities() {
		//given
		saveCityForRepo();
		//then
		List<CityDto> cityDtoList = cityController.displayCurrentWeatherForAllCities().getBody();
		//then
		Assertions.assertEquals(HttpStatusCode.valueOf(200), cityController.displayCurrentWeatherForAllCities().getStatusCode());
		Assertions.assertEquals(1, cityDtoList.size());
	}


	@Test
	void shouldDisplayWeatherParametersForEnteredCity() {
		//given
		saveCityForRepo();
		//when
		CurrentWeatherDto currentWeatherDto = cityController.displayCurrentWeather("Warsaw").getBody();
		//then
		Assertions.assertEquals(HttpStatusCode.valueOf(200),cityController.displayCurrentWeather("Warsaw").getStatusCode());
		Assertions.assertEquals("Warsaw", currentWeatherDto.cityName());
		Assertions.assertEquals(70, currentWeatherDto.humidityPercentage());
		Assertions.assertEquals(25.3, currentWeatherDto.celsiusTemp());
		Assertions.assertEquals(1.54, currentWeatherDto.speedWind());
	}


	 @Test
	 public void shouldThrowNotExistExceptionWhenCityNotExists(){
		Assertions.assertThrows(CityNotExistsException.class,()-> cityController.displayCurrentWeather("Moscow").getBody());
	 }

	@Test
	public void shouldDisplayWeatherForecastForEnteredCity() {
	//given
	saveCityForRepo();
	//when
	List<HourlyWeatherDto> hourlyWeatherDto = cityController.displayHourlyWeather("Warsaw").getBody();

	Assertions.assertEquals(HttpStatusCode.valueOf(200),cityController.displayHourlyWeather("Warsaw").getStatusCode());
	Assertions.assertEquals(28,hourlyWeatherDto.stream().map(HourlyWeatherDto::celsiusTemp).findFirst().get());
	Assertions.assertEquals(80,hourlyWeatherDto.stream().map(HourlyWeatherDto::humidityPercentage).findFirst().get());
	Assertions.assertEquals(1.05,hourlyWeatherDto.stream().map(HourlyWeatherDto::windSpeed).findFirst().get());
	Assertions.assertEquals("2024-08-04 12:00:00",hourlyWeatherDto.stream().map(HourlyWeatherDto::dataTime).findFirst().get());
	}




	@Test
	void test() {
		//given
		saveCityForRepo();
		WireMock.stubFor(get(urlEqualTo("?lat=52.150000&lon=21.050000&appid=ecd5eddecfc5ae1bf0667d186c6bafff&units=metric"))
				.willReturn(aResponse()
						.withStatus(200)
						.withHeader("Content-Type", "application/json")
						.withBody(json())
				));
		//when
		CityDto cityDto = cityService.updateAllForecastForChosenCity("Warsaw");
		//then
		Assertions.assertEquals("Warsaw", cityDto.cityName());
		Assertions.assertEquals(28.87, cityDto.currentWeather().getCurrentWeatherList().stream()
				.map(main -> main.getMain().getTemperature()).findFirst().get());
//		this.webTestClient
//				.put()
//				.uri("api/update/Warsaw")
//				.exchange()
//				.expectStatus().isOk()
//				.expectBody()
//				.jsonPath("$.cityName").isEqualTo("Warsaw")
//				.jsonPath("$.currentWeather.currentWeatherList[0].main.temp").isEqualTo(28.87)
//				.jsonPath("$.currentWeather.currentWeatherList[0].main.humidity").isEqualTo(34)
//				.jsonPath("$.currentWeather.currentWeatherList[0].wind.speed").isEqualTo(3.75);
//				verify(patchRequestedFor(urlEqualTo("/api/update/Warsaw")));
	}

	private void saveCityForRepo(){
		WeatherForecast weatherForecast = new WeatherForecast(List.of(createWeatherForecast()));;
		CurrentWeather currentWeather = new CurrentWeather(List.of(createCurrentWeather()));
		City city = new City(1L,"Warsaw",52.15,21.05,currentWeather,weatherForecast,LocalDateTime.now());
		cityRepository.save(city);
	}

	private CurrentWeatherParameters createCurrentWeather(){
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

	private WeatherForecastHourly createWeatherForecast(){
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

	private String json(){
		return "{\n" +
				"  \"cityName\": \"Warsaw\",\n" +
				"  \"coordinateX\": 52.15,\n" +
				"  \"coordinateY\": 21.02,\n" +
				"  \"currentWeather\": {\n" +
				"    \"currentWeatherList\": [\n" +
				"      {\n" +
				"        \"main\": {\n" +
				"          \"temp\": 28.87,\n" +
				"          \"humidity\": 34\n" +
				"        },\n" +
				"        \"wind\": {\n" +
				"          \"speed\": 3.75\n" +
				"        }\n" +
				"      }\n" +
				"    ]\n" +
				"  },\n" +
				"  \"hourlyCurrentWeather\": {\n" +
				"    \"weatherForecastHourlyParametersList\": [\n" +
				"      {\n" +
				"        \"list\": [\n" +
				"          {\n" +
				"            \"main\": {\n" +
				"              \"temp\": 29.14,\n" +
				"              \"humidity\": 35\n" +
				"            },\n" +
				"            \"wind\": {\n" +
				"              \"speed\": 4.35\n" +
				"            },\n" +
				"            \"dt_txt\": \"2024-08-04 12:00:00\"\n" +
				"          }\n" +
				"        ]\n" +
				"      }\n" +
				"    ]\n" +
				"  }\n" +
				"}";
	}


}
