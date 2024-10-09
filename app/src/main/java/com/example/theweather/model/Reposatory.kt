package com.example.theweather.model

import com.example.theweather.db.FavouriteLocationsLocalDataSource
import com.example.theweather.db.IFavouriteLocationsLocalDataSource
import com.example.theweather.network.IRemoteDataSource
import com.example.theweather.network.RemoteDataSource
import kotlinx.coroutines.flow.Flow
import org.intellij.lang.annotations.Language

class Reposatory (private val remoteDataSource: IRemoteDataSource , private val localDataSource: IFavouriteLocationsLocalDataSource) :
    IReposatory {

    companion object {
        private var instance: Reposatory? = null
        fun getInstance(
            remoteDataSource: IRemoteDataSource ,localDataSource:IFavouriteLocationsLocalDataSource
        ): Reposatory {
            return instance ?: synchronized(this) {
                val temp = Reposatory(remoteDataSource,localDataSource)
                instance = temp
                temp
            }
        }
    }

    override suspend fun getWitherOfTheDay(lat:Double, long:Double, language: String): Flow<CurrentDayWeatherResponce>
    {
        return remoteDataSource.getTodayForecast(lat,long,language)
    }

    override suspend fun getFiveDaysForecast(lat:Double, long:Double, language: String): Flow<ForecastResponse>
    {
        return remoteDataSource.getAllFiveDaysForecast (lat,long,language)
    }

    override suspend fun insertToFavourie (favItem : FavouriteLocationItem) :Long
    {
        return localDataSource.insert(favItem)
    }

    override suspend fun deleteFromFavourie (favItem : FavouriteLocationItem) : Int
    {
        return localDataSource.delete(favItem)
    }
    override fun getAllFavouriteLocations (): Flow<List<FavouriteLocationItem>?>
    {
        return localDataSource.getAllFavouriteProduct()
    }




}