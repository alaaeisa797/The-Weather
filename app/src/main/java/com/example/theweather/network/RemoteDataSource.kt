package com.example.theweather.network

import com.example.theweather.model.CurrentDayWeatherResponce
import com.example.theweather.model.ForecastResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class RemoteDataSource private constructor ( private val netWorkService: NetWorkService ) {

    suspend fun getTodayForecast(lat :Double, lon:Double,language: String): Flow<CurrentDayWeatherResponce> {
        return flow {
            val result = netWorkService.getCurrentWither(lat,lon,language)
            emit(result)
        }
    }

    suspend fun getAllFiveDaysForecast (lat :Double, lon:Double,language: String):Flow<ForecastResponse>
    {
        return flow {
            val result = netWorkService.getForecast (lat,lon,language)
            emit(result)
        }

    }


    companion object
    {
        private var instance :RemoteDataSource? = null
        fun getInstance(netWorkService: NetWorkService):RemoteDataSource{

            return instance?: synchronized(this){
                val temp = RemoteDataSource(netWorkService)
                instance =temp
                return temp
            }
        }
    }
}