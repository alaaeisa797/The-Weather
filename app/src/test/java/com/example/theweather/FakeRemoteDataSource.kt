package com.example.theweather

import com.example.theweather.model.CurrentDayWeatherResponce
import com.example.theweather.model.ForecastResponse
import com.example.theweather.network.IRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeRemoteDataSource
    ( private var currentDayWeather : CurrentDayWeatherResponce , private var  forecastResponce: ForecastResponse)
    : IRemoteDataSource{
    override suspend fun getTodayForecast(
        lat: Double,
        lon: Double,
        language: String
    ): Flow<CurrentDayWeatherResponce> {
       return flowOf(currentDayWeather)
    }

    override suspend fun getAllFiveDaysForecast(
        lat: Double,
        lon: Double,
        language: String
    ): Flow<ForecastResponse> {
       return flowOf(forecastResponce)
    }
}