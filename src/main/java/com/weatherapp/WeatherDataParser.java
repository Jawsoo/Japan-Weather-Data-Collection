package com.weatherapp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WeatherDataParser {
    private static final Logger logger = LoggerFactory.getLogger(WeatherDataParser.class);

    public static WeatherData parseWeatherData(String json, String city) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(json);

            double temperature = rootNode.path("main").path("temp").asDouble();
            int humidity = rootNode.path("main").path("humidity").asInt();
            String weatherCondition = rootNode.path("weather").get(0).path("description").asText();
            double feelsLike = rootNode.path("main").path("feels_like").asDouble();
            int pressure = rootNode.path("main").path("pressure").asInt();
            double windSpeed = rootNode.path("wind").path("speed").asDouble();

            // Extract precipitation (rain/snow)
            Double precipitation = null;
            if (rootNode.has("rain") && rootNode.path("rain").has("1h")) {
                precipitation = rootNode.path("rain").path("1h").asDouble();
            } else if (rootNode.has("snow") && rootNode.path("snow").has("1h")) {
                precipitation = rootNode.path("snow").path("1h").asDouble();
            }

            // Extract additional optional data
            Integer windDirection = rootNode.path("wind").has("deg") ? rootNode.path("wind").path("deg").asInt() : null;
            Double windGust = rootNode.path("wind").has("gust") ? rootNode.path("wind").path("gust").asDouble() : null;
            Integer clouds = rootNode.path("clouds").has("all") ? rootNode.path("clouds").path("all").asInt() : null;
            Integer visibility = rootNode.path("visibility").isMissingNode() ? null : rootNode.path("visibility").asInt();
            Long sunrise = rootNode.path("sys").has("sunrise") ? rootNode.path("sys").path("sunrise").asLong() : null;
            Long sunset = rootNode.path("sys").has("sunset") ? rootNode.path("sys").path("sunset").asLong() : null;

            return new WeatherData(city, temperature, feelsLike, humidity, pressure, windSpeed,
                    windDirection, windGust, clouds, visibility, sunrise, sunset, weatherCondition, precipitation);
        } catch (Exception e) {
            logger.error("❌ Error parsing weather data for {}: {}", city, e.getMessage());
            return null;
        }
    }
}
