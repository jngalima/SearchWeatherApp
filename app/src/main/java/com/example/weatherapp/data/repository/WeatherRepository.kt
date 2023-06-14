package com.example.weatherapp.data.repository

import android.graphics.Bitmap
import com.example.weatherapp.data.repository.IconCache
import com.example.weatherapp.data.api.model.Result
import com.example.weatherapp.data.api.model.WeatherDataResponse
import com.example.weatherapp.data.api.service.WeatherService
import com.example.weatherapp.data.remoteDataSource.RemoteDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class WeatherRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val iconCache: IconCache
) {
    suspend fun fetchWeatherData(paramsMap: Map<String, String>): Result<WeatherDataResponse>{
        return withContext(Dispatchers.IO){
            try {
                Result.Success(remoteDataSource.fetchWeatherData(paramsMap))
            } catch (e: Exception){
                Result.Error(e)
            }
        }
    }

    fun fetchIconFromCache(iconId: String) = iconCache.getImageFromCache(iconId)

    fun addIconToCache(iconId: String, bitmap: Bitmap) = iconCache.addToCache(iconId, bitmap)
}