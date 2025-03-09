package com.weatherapp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WeatherDatabaseManager {
    private static final String URL = "jdbc:mysql://localhost:3306/weather_db";
    private static final String USER = "root";
    private static final String PASSWORD = "9902";

    private static final Logger logger = LoggerFactory.getLogger(WeatherDatabaseManager.class);

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            logger.error("❌ MySQL JDBC Driver not found!", e);
        }
    }

    public static void insertWeatherData(WeatherData weatherData) {
        if (weatherData == null) {
            logger.warn("⚠️ WeatherData is null. Skipping database insertion.");
            return;
        }

        String query = "INSERT INTO weather_data (city, temperature, feels_like, humidity, pressure, wind_speed, " +
                "wind_direction, wind_gust, clouds, visibility, sunrise, sunset, weather_condition, precipitation) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, weatherData.getCity());
            stmt.setDouble(2, weatherData.getTemperature());
            stmt.setDouble(3, weatherData.getFeelsLike());
            stmt.setInt(4, weatherData.getHumidity());
            stmt.setInt(5, weatherData.getPressure());
            stmt.setDouble(6, weatherData.getWindSpeed());

            // Handle NULL values
            if (weatherData.getWindDirection() == null) stmt.setNull(7, java.sql.Types.INTEGER);
            else stmt.setInt(7, weatherData.getWindDirection());

            if (weatherData.getWindGust() == null) stmt.setNull(8, java.sql.Types.FLOAT);
            else stmt.setDouble(8, weatherData.getWindGust());

            if (weatherData.getCloudCover() == null) stmt.setNull(9, java.sql.Types.INTEGER);
            else stmt.setInt(9, weatherData.getCloudCover());

            if (weatherData.getVisibility() == null) stmt.setNull(10, java.sql.Types.INTEGER);
            else stmt.setInt(10, weatherData.getVisibility());

            if (weatherData.getSunrise() == null) stmt.setNull(11, java.sql.Types.BIGINT);
            else stmt.setLong(11, weatherData.getSunrise());

            if (weatherData.getSunset() == null) stmt.setNull(12, java.sql.Types.BIGINT);
            else stmt.setLong(12, weatherData.getSunset());

            stmt.setString(13, weatherData.getWeatherCondition());

            if (weatherData.getPrecipitation() == null) stmt.setNull(14, java.sql.Types.FLOAT);
            else stmt.setDouble(14, weatherData.getPrecipitation());

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                logger.info("✅ Successfully inserted weather data for {}", weatherData.getCity());
            }
        } catch (SQLException e) {
            logger.error("❌ Error inserting weather data for {}: {}", weatherData.getCity(), e.getMessage());
        }
    }
}
