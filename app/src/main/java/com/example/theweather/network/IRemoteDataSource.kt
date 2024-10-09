package com.example.theweather.network

import com.example.theweather.model.CurrentDayWeatherResponce
import com.example.theweather.model.ForecastResponse
import kotlinx.coroutines.flow.Flow

interface IRemoteDataSource {
    suspend fun getTodayForecast(
        lat: Double,
        lon: Double,
        language: String
    ): Flow<CurrentDayWeatherResponce>

    suspend fun getAllFiveDaysForecast(
        lat: Double,
        lon: Double,
        language: String
    ): Flow<ForecastResponse>


}