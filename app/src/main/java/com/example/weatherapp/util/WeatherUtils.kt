package com.example.weatherapp.util

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.weatherapp.data.api.model.WeatherDataResponse
import com.example.weatherapp.model.WeatherDetailsUIModel

fun WeatherDataResponse.toUIModel(): WeatherDetailsUIModel =
    WeatherDetailsUIModel(
        city = cityName,
        temperature = main?.temp?.toInt(),
        icon = weather?.firstOrNull()?.icon,
        weatherDescription = weather?.firstOrNull()?.description,
        feelsLike = main?.feelsLike?.toInt(),
        minTemp = main?.minTemp?.toInt(),
        maxTemp = main?.maxTemp?.toInt(),
        humidity = main?.humidity,
        windSpeed = wind?.speed?.toInt(),
        sunrise = sys?.sunrise,
        sunset = sys?.sunset
    )