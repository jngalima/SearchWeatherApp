package com.example.weatherapp

import android.graphics.Bitmap
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.weatherapp.data.api.model.Result
import com.example.weatherapp.data.api.model.WeatherDataResponse
import com.example.weatherapp.data.api.service.WeatherService
import com.example.weatherapp.data.remoteDataSource.RemoteDataSource
import com.example.weatherapp.data.repository.IconCache
import com.example.weatherapp.data.repository.WeatherRepository
import com.example.weatherapp.util.CITY_NAME
import com.example.weatherapp.util.UNITS
import com.example.weatherapp.viewModel.WeatherViewModel
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.Spy


/**
 * Added some unit tests for the viewModel
 * Given more time I would add more tests
 * and create test classes for repository, remote data source, and service
 */
class WeatherViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var weatherViewModel: WeatherViewModel

    @Mock
    lateinit var mockWeatherRepository: WeatherRepository

    private val testScope = TestCoroutineScope(TestCoroutineDispatcher())

    @Before
    fun setup(){
        MockitoAnnotations.openMocks(this)
        weatherViewModel = WeatherViewModel(mockWeatherRepository)
    }

    @Test
    fun `test fetch weather data sucess`() {
        testScope.runBlockingTest {
            Mockito.`when`(mockWeatherRepository.fetchWeatherData(mapOf(CITY_NAME to "London")))
                .thenReturn(Result.Success(WeatherDataResponse(cityName = "London")))

            val mockWeatherData = Result.Success(WeatherDataResponse(cityName = "London"))

            val mockObserver = Observer<Result<WeatherDataResponse>?> {
                assertEquals(mockWeatherData, it)
            }

            weatherViewModel.weatherData.observeForever(mockObserver)

            weatherViewModel.loadWeatherData(mapOf(CITY_NAME to "London"))

            weatherViewModel.weatherData.removeObserver(mockObserver)
        }
    }

    @Test
    fun `test fetch weather data failure`() {
        testScope.runBlockingTest {
            Mockito.`when`(mockWeatherRepository.fetchWeatherData(mapOf(CITY_NAME to "London")))
                .thenReturn(Result.Error(Exception("Error fetching weather data")))

            val mockWeatherData = Result.Error(Exception("Error fetching weather data"))

            val mockObserver = Observer<Result<WeatherDataResponse>?> {
                assertEquals(mockWeatherData, it)
            }

            weatherViewModel.weatherData.observeForever(mockObserver)

            weatherViewModel.loadWeatherData(mapOf(CITY_NAME to "London"))

            weatherViewModel.weatherData.removeObserver(mockObserver)
        }
    }

    @Test
    fun `test showLoadingScreen() when value is true`() {
        testScope.runBlockingTest {

            val isLoading = true

            val mockObserver = Observer<Boolean> {
                assertEquals(isLoading, it)
            }

            weatherViewModel.isLoading.observeForever(mockObserver)

            weatherViewModel.showLoadingScreen(true)

            weatherViewModel.isLoading.removeObserver(mockObserver)
        }
    }

    @Test
    fun `test showLoadingScreen() when value is false`() {
        testScope.runBlockingTest {

            val isLoading = false

            val mockObserver = Observer<Boolean> {
                assertEquals(isLoading, it)
            }

            weatherViewModel.isLoading.observeForever(mockObserver)

            weatherViewModel.showLoadingScreen(false)

            weatherViewModel.isLoading.removeObserver(mockObserver)
        }
    }
}