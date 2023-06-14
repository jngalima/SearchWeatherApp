package com.example.weatherapp.data.api.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * For the sake of time and simplicity
 * Only relevant data is consumed from api response
 */

@JsonClass(generateAdapter = true)
data class WeatherDataResponse(
    @Json(name="name")
    val cityName: String? = null,
    val weather: List<Weather>? = null,
    val main: Main? = null,
    val wind: Wind? = null,
    @Json(name="dt")
    val dateTime: Long? = null,
    val sys: Sys? = null
)

@JsonClass(generateAdapter = true)
data class Weather(
    val main: String? = null,
    val description: String? = null,
    val icon: String? = null
)

@JsonClass(generateAdapter = true)
data class Main(
    val temp: Double? = null,
    @Json(name="feels_like")
    val feelsLike: Double? = null,
    @Json(name="temp_min")
    val minTemp: Double? = null,
    @Json(name="temp_max")
    val maxTemp: Double? = null,
    val pressure: Int? = null,
    val humidity: Int? = null,
)

@JsonClass(generateAdapter = true)
data class Wind(
    val speed: Double? = null,
    val deg: Int? = null,
    val gust: String? = null
)

@JsonClass(generateAdapter = true)
data class Sys(
    val sunrise: Long? = null,
    val sunset: Long? = null
)