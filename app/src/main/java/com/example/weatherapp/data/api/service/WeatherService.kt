package com.example.weatherapp.data.api.service

import com.example.weatherapp.data.api.model.WeatherDataResponse
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.QueryMap
import retrofit2.http.Url

interface WeatherService{

    @GET("data/2.5/weather/")
    suspend fun fetchWeatherData(@QueryMap paramsMap: Map<String, String>): WeatherDataResponse
}