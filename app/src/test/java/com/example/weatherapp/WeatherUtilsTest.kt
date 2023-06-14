package com.example.weatherapp

import com.example.weatherapp.data.api.model.*
import com.example.weatherapp.model.WeatherDetailsUIModel
import com.example.weatherapp.util.toUIModel
import junit.framework.Assert.assertEquals
import org.junit.Test

class WeatherUtilsTest {
    @Test
    fun `Successful WeatherResponse properly maps to UI model`() {
        val weatherUIModel = WeatherDataResponse(
            cityName="Broadmoor",
            weather= listOf(Weather(main="Clouds", description="broken clouds", icon="04d")),
            main= Main(temp=55.0, feelsLike=54.34, minTemp=52.57, maxTemp=61.38, pressure=1015, humidity=88),
            wind= Wind(speed=14.97, deg=280, gust=null), dateTime=1686713475,
            sys= Sys(sunrise=1686660471, sunset=1686713533)
        ).toUIModel()

        assertEquals(weatherUIModel, WeatherDetailsUIModel(
            city="Broadmoor",
            temperature=55,
            icon="04d",
            weatherDescription="broken clouds",
            feelsLike=54,
            minTemp=52,
            maxTemp=61,
            humidity=88,
            windSpeed=14,
            sunrise=1686660471,
            sunset=1686713533
            )
        )
    }
}