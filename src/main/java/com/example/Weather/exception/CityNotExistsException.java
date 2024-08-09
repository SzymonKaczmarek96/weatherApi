package com.example.Weather.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CityNotExistsException extends RuntimeException {
    public CityNotExistsException(String cityName) {
        super(cityName + " doesn't exists in DB");
    }
}
