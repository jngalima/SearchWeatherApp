package com.example.weatherapp.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.example.weatherapp.data.repository.IconCache
import com.example.weatherapp.data.repository.WeatherRepository
import com.example.weatherapp.data.api.service.WeatherService
import com.example.weatherapp.data.remoteDataSource.RemoteDataSource
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
open class WeatherModule {
    companion object{
        private const val BASE_URL = "https://api.openweathermap.org/"
        private const val API_KEY = "2f2e037102404487b781d5db0a778463"
        private const val APP_ID = "appid"
    }

    @Provides
    @Singleton
    fun provideApiKeyInterceptor(): Interceptor {
        return Interceptor { chain ->
            val newUrl = chain.request().url
                .newBuilder()
                .addQueryParameter(APP_ID, API_KEY)
                .build()

            val newRequest = chain.request()
                .newBuilder()
                .url(newUrl)
                .build()

            chain.proceed(newRequest)
        }
    }

    @Provides
    @Singleton
    open fun provideHttpClient(apiKeyInterceptor: Interceptor): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(apiKeyInterceptor)
        .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
        .build()

    @Provides
    @Singleton
    open fun provideMoshi(): Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    @Provides
    @Singleton
    open fun provideWeatherService(moshi: Moshi, httpClient: OkHttpClient): WeatherService = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(httpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
        .create(WeatherService::class.java)

    @Provides
    @Singleton
    open fun provideWeatherRepository(
        remoteDataSource: RemoteDataSource,
        iconCache: IconCache
    ): WeatherRepository = WeatherRepository(remoteDataSource, iconCache)

    @Provides
    @Singleton
    open fun provideRemoteDataSource(
        weatherService: WeatherService
    ): RemoteDataSource = RemoteDataSource(weatherService)

    @Provides
    @Singleton
    open fun provideSharedPreferences(application: Application): SharedPreferences =
        application.getSharedPreferences("shared_preferences", Context.MODE_PRIVATE)

    @Provides
    @Singleton
    open fun provideIconCache(): IconCache = IconCache()
}