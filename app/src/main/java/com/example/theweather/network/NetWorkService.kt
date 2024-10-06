package com.example.theweather.network


import com.example.theweather.model.CurrentDayWeatherResponce
import com.example.theweather.model.ForecastResponse
import retrofit2.http.GET
import retrofit2.http.Query

private const val MyApiKey = "6a70046577f9218c0128ee97a668fda0"
interface NetWorkService {
    @GET("weather")
    suspend fun getCurrentWither(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("lang") lang: String = "en",
        @Query("appid") apiKey: String = MyApiKey,
        @Query("units") units: String = "metric"

    ) : CurrentDayWeatherResponce

    @GET("forecast")
    suspend fun getForecast(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("lang") lang: String = "en",
        @Query("appid") apiKey: String = MyApiKey,
        @Query("units") units: String = "metric"

    ) : ForecastResponse



}