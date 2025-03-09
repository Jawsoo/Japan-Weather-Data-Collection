package com.weatherapp;

public class WeatherData {
    private final String city;
    private final double temperature;
    private final double feelsLike;
    private final int humidity;
    private final int pressure;
    private final double windSpeed;
    private final Integer windDirection;  // Nullable
    private final Double windGust;        // Nullable
    private final Integer cloudCover;     // Nullable
    private final Integer visibility;     // Nullable
    private final Long sunrise;           // Nullable
    private final Long sunset;            // Nullable
    private final String weatherCondition;
    private final Double precipitation;   // Nullable

    // Constructor
    public WeatherData(String city, double temperature, double feelsLike, int humidity, int pressure,
                       double windSpeed, Integer windDirection, Double windGust, Integer cloudCover,
                       Integer visibility, Long sunrise, Long sunset, String weatherCondition, Double precipitation) {
        this.city = city;
        this.temperature = temperature;
        this.feelsLike = feelsLike;
        this.humidity = humidity;
        this.pressure = pressure;
        this.windSpeed = windSpeed;
        this.windDirection = windDirection;
        this.windGust = windGust;
        this.cloudCover = cloudCover;
        this.visibility = visibility;
        this.sunrise = sunrise;
        this.sunset = sunset;
        this.weatherCondition = weatherCondition;
        this.precipitation = precipitation;
    }

    // Getters
    public String getCity() { return city; }
    public double getTemperature() { return temperature; }
    public double getFeelsLike() { return feelsLike; }
    public int getHumidity() { return humidity; }
    public int getPressure() { return pressure; }
    public double getWindSpeed() { return windSpeed; }
    public Integer getWindDirection() { return windDirection; }
    public Double getWindGust() { return windGust; }
    public Integer getCloudCover() { return cloudCover; }
    public Integer getVisibility() { return visibility; }
    public Long getSunrise() { return sunrise; }
    public Long getSunset() { return sunset; }
    public String getWeatherCondition() { return weatherCondition; }
    public Double getPrecipitation() { return precipitation; }
}
