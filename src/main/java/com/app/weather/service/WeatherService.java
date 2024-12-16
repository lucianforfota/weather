package com.app.weather.service;

import com.app.weather.dto.CurrentWeatherDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

@Service
public class WeatherService {

    private RestTemplate restTemplate;

    private ObjectMapper objectMapper;

    private static final String BASE_URL ="https://api.tomorrow.io/v4/weather";

    @Autowired
    public WeatherService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper=objectMapper;
    }

    @Value("${weather.api}")
    private String apiKey;

    public CurrentWeatherDTO getCurrentWeather(double lat, double lon) throws JsonProcessingException {

        //String url = "https://api.tomorrow.io/v4/weather/realtime?location="+lat+","+lon+"&apikey="+apiKey;
        String url = UriComponentsBuilder
                .fromUriString(BASE_URL+"/realtime")
                .queryParam("location",lat+","+lon)
                .queryParam("apikey", apiKey)
                .toUriString();

        //facem request catre api-ul extern (folosind url-ul pe care tocmai l-am creat) de la tommorow punand-ul pe restTemplate sa faca asta si primim raspunsul sub forma de string
        String response = restTemplate.getForObject(url,String.class);
        //transformam string-ul obtinut ca raspund intr-un jsnode node ca sa il putem aprcurge ca un arbore si sa extragem ingomratii
        JsonNode root = objectMapper.readTree(response);
        return mapFromJsonToCurrentWeatherDTO(root);
    }

    public List<CurrentWeatherDTO> getForecastWeather(double lat, double lon) throws JsonProcessingException {

        //String url = "https://api.tomorrow.io/v4/weather/realtime?location="+lat+","+lon+"&apikey="+apiKey;
        String url = UriComponentsBuilder
                .fromUriString(BASE_URL+"/forecast")
                .queryParam("location",lat+","+lon)
                .queryParam("timesteps", "1d")
                .queryParam("apikey", apiKey)
                .toUriString();

        //facem request catre api-ul extern (folosind url-ul pe care tocmai l-am creat) de la tommorow punand-ul pe restTemplate sa faca asta si primim raspunsul sub forma de string
        String response = restTemplate.getForObject(url,String.class);
        //transformam string-ul obtinut ca raspund intr-un jsnode node ca sa il putem aprcurge ca un arbore si sa extragem ingomratii
        JsonNode root = objectMapper.readTree(response);

        //obtin array-ul de noduri de la timelines -> daily
        ArrayNode forecasts = (ArrayNode) root.path("timelines").path("daily");
        List<CurrentWeatherDTO> currentWeatherDTOS = new ArrayList<>();
        //parcurg fiecare nod
            for ( JsonNode jsonNode: forecasts){
                //ma duc la values->temperature si iau valoarea temperaturii
                //ma duc la values->humidity si iau umiditatea
                //fac cu valorile extrase un currentweatherdto si il bag in lista rezultat
                double temp = jsonNode.path("values").path("temperature").asDouble();
                double humidity = jsonNode.path("values").path("humidity").asDouble();
                currentWeatherDTOS.add(new CurrentWeatherDTO(temp,humidity));
            }
        //returnez lista de current weeather dto obtinuta
        return currentWeatherDTOS;
    }


    public CurrentWeatherDTO mapFromJsonToCurrentWeatherDTO(JsonNode root){
        double humidity= root.path("data").path("values").path("humidity").asDouble();
        double temperature= root.path("data").path("values").path("temperature").asDouble();
        return new CurrentWeatherDTO(temperature,humidity);
    }
}
