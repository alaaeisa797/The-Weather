package com.example.theweather

import com.example.theweather.model.CurrentDayWeatherResponce
import com.example.theweather.model.FavouriteLocationItem
import com.example.theweather.model.ForecastResponse
import com.example.theweather.model.IReposatory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeReposatory (private var fakeRemoteDataSource: FakeRemoteDataSource,
                      private  var fakeLocalDataSource: FakeLocalDataSource) :IReposatory {
    override suspend fun getWitherOfTheDay(
        lat: Double,
        long: Double,
        language: String
    ): Flow<CurrentDayWeatherResponce> {
        return fakeRemoteDataSource.getTodayForecast (lat,long,language)
    }

    override suspend fun getFiveDaysForecast(
        lat: Double,
        long: Double,
        language: String
    ): Flow<ForecastResponse> {
       return fakeRemoteDataSource.getAllFiveDaysForecast (lat,long,language)
    }

    override suspend fun insertToFavourie(favItem: FavouriteLocationItem): Long {
       return fakeLocalDataSource.insert(favItem)
    }

    override suspend fun deleteFromFavourie(favItem: FavouriteLocationItem): Int {
        TODO("Not yet implemented")
    }

    override fun getAllFavouriteLocations(): Flow<List<FavouriteLocationItem>?> {
        TODO("Not yet implemented")
    }


}