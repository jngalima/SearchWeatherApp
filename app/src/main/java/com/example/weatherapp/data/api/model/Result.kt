package com.example.weatherapp.data.api.model

sealed class Result<out T> {
    data class Success<out T>(val data: T): Result<T>()

    data class Error<out T>(val error: T): Result<Nothing>()

    object Loading: Result<Nothing>()
}