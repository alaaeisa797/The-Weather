package com.example.theweather.model

import com.example.theweather.db.FavouriteLocationsLocalDataSource
import com.example.theweather.network.RemoteDataSource
import kotlinx.coroutines.flow.Flow
import org.intellij.lang.annotations.Language

class Reposatory private constructor(private val remoteDataSource: RemoteDataSource , private val localDataSource: FavouriteLocationsLocalDataSource) {

    companion object {
        private var instance: Reposatory? = null
        fun getInstance(
            remoteDataSource: RemoteDataSource ,localDataSource:FavouriteLocationsLocalDataSource
        ): Reposatory {
            return instance ?: synchronized(this) {
                val temp = Reposatory(remoteDataSource,localDataSource)
                instance = temp
                temp
            }
        }
    }

    suspend fun getWitherOfTheDay(lat:Double,long:Double,language: String): Flow<CurrentDayWeatherResponce>
    {
        return remoteDataSource.getTodayForecast(lat,long,language)
    }

    suspend fun getFiveDaysForecast(lat:Double,long:Double,language: String): Flow<ForecastResponse>
    {
        return remoteDataSource.getAllFiveDaysForecast (lat,long,language)
    }

    suspend fun insertToFavourie (favItem : FavouriteLocationItem) :Long
    {
        return localDataSource.insert(favItem)
    }

    suspend fun deleteFromFavourie (favItem : FavouriteLocationItem) : Int
    {
        return localDataSource.delete(favItem)
    }
    fun getAllFavouriteLocations (): Flow<List<FavouriteLocationItem>?>
    {
        return localDataSource.getAllFavouriteProduct()
    }




}