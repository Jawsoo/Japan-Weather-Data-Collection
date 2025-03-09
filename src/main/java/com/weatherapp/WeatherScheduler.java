package com.weatherapp;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WeatherScheduler {
    private static final List<String> JAPAN_CITIES = Arrays.asList(
            "Tokyo", "Osaka", "Kyoto", "Sapporo", "Fukuoka",
            "Nagoya", "Yokohama", "Sendai", "Hiroshima", "Kobe",
            "Nagasaki", "Kagoshima", "Niigata", "Shizuoka", "Okayama",
            "Utsunomiya", "Matsuyama", "Okinawa", "Fukushima", "Kanazawa"
    );

    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static final Logger logger = LoggerFactory.getLogger(WeatherScheduler.class);

    public static void main(String[] args) {
        Runnable fetchWeatherTask = () -> {
            for (String city : JAPAN_CITIES) {
                try {
                    String jsonResponse = WeatherApiClient.fetchWeatherData(city);
                    if (jsonResponse != null && !jsonResponse.isEmpty()) {
                        WeatherData weatherData = WeatherDataParser.parseWeatherData(jsonResponse, city);

                        if (weatherData != null) {
                            // Insert weather data into MySQL database
                            WeatherDatabaseManager.insertWeatherData(weatherData);

                            // Simulated past temperature values
                            double[] pastTemps = {weatherData.getTemperature(), weatherData.getTemperature() + 1,
                                    weatherData.getTemperature() - 1, weatherData.getTemperature() + 2,
                                    weatherData.getTemperature() - 2};

                            double[] predictedTemps = LinearRegressionModel.predict(pastTemps);
                            WeatherChart.createChart(city, predictedTemps);
                        }
                    } else {
                        logger.warn("No data received for {}", city);
                    }
                } catch (Exception e) {
                    logger.error("Error fetching weather data for {}: {}", city, e.getMessage(), e);
                }
            }
        };

        scheduler.scheduleAtFixedRate(fetchWeatherTask, 0, 10, TimeUnit.MINUTES);
    }
}
