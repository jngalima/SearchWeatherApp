package com.example.weatherapp.data.remoteDataSource

import com.example.weatherapp.data.api.model.WeatherDataResponse
import com.example.weatherapp.data.api.service.WeatherService
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val weatherService: WeatherService
) {
    suspend fun fetchWeatherData(paramsMap: Map<String, String>): WeatherDataResponse =
        weatherService.fetchWeatherData(paramsMap)
}