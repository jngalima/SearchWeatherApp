package com.example.weatherapp.model

data class WeatherDetailsUIModel(
    val city: String? = null,
    val temperature: Int? = null,
    val icon: String? = null,
    val weatherDescription: String? = null,
    val feelsLike: Int? = null,
    val minTemp: Int? = null,
    val maxTemp: Int? = null,
    val humidity: Int? = null,
    val windSpeed: Int? = null,
    val sunrise: Long? = null,
    val sunset: Long? = null
)