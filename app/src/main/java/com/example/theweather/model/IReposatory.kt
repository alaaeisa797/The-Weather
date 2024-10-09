package com.example.theweather.model

import kotlinx.coroutines.flow.Flow

interface IReposatory {
    suspend fun getWitherOfTheDay(
        lat: Double,
        long: Double,
        language: String
    ): Flow<CurrentDayWeatherResponce>

    suspend fun getFiveDaysForecast(
        lat: Double,
        long: Double,
        language: String
    ): Flow<ForecastResponse>

    suspend fun insertToFavourie(favItem: FavouriteLocationItem): Long

    suspend fun deleteFromFavourie(favItem: FavouriteLocationItem): Int

    fun getAllFavouriteLocations(): Flow<List<FavouriteLocationItem>?>
}