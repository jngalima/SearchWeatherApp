package com.example.weatherapp.viewModel

import android.content.SharedPreferences
import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.repository.WeatherRepository
import com.example.weatherapp.data.api.model.WeatherDataResponse
import com.example.weatherapp.data.api.model.Result
import com.example.weatherapp.util.CITY_NAME
import com.example.weatherapp.util.UNITS
import com.squareup.picasso.Picasso
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository
): ViewModel() {

    /**
     * Last searched location will be stored here
     */
    @Inject
    lateinit var sharedPreferences: SharedPreferences

    /**
     * live data to hold response from api
     */
    private val _weatherData = MutableLiveData<Result<WeatherDataResponse>?>()
    val weatherData: LiveData<Result<WeatherDataResponse>?> = _weatherData

    /**
     * live data to track when screen should be loading
     */
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    /**
     * will contain parameters for api call like city name, units, lon, lat
     */
    private var paramsMap = mapOf<String, String>()

    /**
     * will contain the icon fetched from cache or api
     */
    private val _iconBitMap = MutableLiveData<Bitmap>()
    val iconBitMap: LiveData<Bitmap> = _iconBitMap

    /**
     * Fetch weather data from api and populate live data with response
     */
    fun loadWeatherData(map: Map<String, String>){
        viewModelScope.launch {
            showLoadingScreen(true)

            paramsMap = map
            val response = weatherRepository.fetchWeatherData(paramsMap)
            _weatherData.postValue(response)

            showLoadingScreen(false)
        }
    }

    /**
     * Triggers loading animation while api call in progress
     */
    fun showLoadingScreen(shouldShow: Boolean){
        _isLoading.postValue(shouldShow)
    }

    /**
     * Check for last searched data in sharedPreferences
     * and load data if exists
     */
    fun processLastSearched(){
        val cityName = sharedPreferences.getString(CITY_NAME, null)
        val units = sharedPreferences.getString(UNITS, null)
        if(cityName != null && units != null){
            val paramsMap = mutableMapOf(
                CITY_NAME to cityName,
                UNITS to units
            )
            loadWeatherData(paramsMap)
        }
    }

    /**
     * Saved last searched lcoation in sharedPreferences
     */
    fun saveLastSearched(){
        val editor = sharedPreferences.edit()
        editor.putString(CITY_NAME, paramsMap[CITY_NAME])
        editor.putString(UNITS, paramsMap[UNITS])
        editor.apply()
    }

    /**
     * Fetch icon from cache
     * If its not in cache, fetch from api
     */
    fun getIcon(iconId: String, url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val bitmap = weatherRepository.fetchIconFromCache(iconId)
                ?: Picasso.get().load(url).get().also {
                    weatherRepository.addIconToCache(iconId, it)
                }
            _iconBitMap.postValue(bitmap)
        }
    }
}
