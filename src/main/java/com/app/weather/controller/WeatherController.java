package com.app.weather.controller;

import com.app.weather.dto.CurrentWeatherDTO;
import com.app.weather.service.WeatherService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/weather")
public class WeatherController {

    private WeatherService weatherService;

    @Autowired
    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/current")
    public ResponseEntity<?> getCurrentWeather(@RequestParam double lat, @RequestParam double lon){
        try {
            CurrentWeatherDTO currentWeatherDTO = weatherService.getCurrentWeather(lat, lon);
            return ResponseEntity.ok(currentWeatherDTO);
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/forecast")
    public ResponseEntity<?> getForecastWeather(@RequestParam double lat, @RequestParam double lon){
        try {
            List<CurrentWeatherDTO> currentWeatherDTOs = weatherService.getForecastWeather(lat, lon);
            return ResponseEntity.ok(currentWeatherDTOs);
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
