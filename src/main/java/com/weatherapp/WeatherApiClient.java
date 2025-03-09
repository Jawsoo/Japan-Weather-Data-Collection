package com.weatherapp;

import org.apache.hc.client5.http.fluent.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WeatherApiClient {
    private static final Logger logger = LoggerFactory.getLogger(WeatherApiClient.class);
    private static final String API_KEY = "24596eb886c82fb2df51c7d1f9f10324";  // API key
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/weather?q=%s&units=metric&appid=%s";

    public static String fetchWeatherData(String city) {
        try {
            String url = String.format(BASE_URL, city, API_KEY);
            logger.info("Fetching weather data from: {}", url);

            String response = Request.get(url).execute().returnContent().asString();
            logger.info("Weather data received for {}: {}", city, response);
            return response;
        } catch (Exception e) {
            logger.error("Error fetching weather data for {}: {}", city, e.getMessage());
            return null;
        }
    }
}
