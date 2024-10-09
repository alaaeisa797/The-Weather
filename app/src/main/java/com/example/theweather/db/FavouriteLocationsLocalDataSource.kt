package com.example.theweather.db


import com.example.theweather.model.AlarmItem
import com.example.theweather.model.FavouriteLocationItem
import kotlinx.coroutines.flow.Flow

class FavouriteLocationsLocalDataSource (val dao : DAO) : IFavouriteLocationsLocalDataSource {

    override suspend fun insert (favItem :FavouriteLocationItem) :Long
    {
       return dao.insert(favItem)
    }

    override suspend fun delete (favItem :FavouriteLocationItem) : Int
    {
       return dao.delete(favItem)
    }

    override fun getAllFavouriteProduct(): Flow<List<FavouriteLocationItem>> {
        return  dao.getAllMyFavouriteLocations()
    }

    suspend fun insertAlert (alertItem : AlarmItem) : Long
    {
        return dao.insertAlarm(alertItem)
    }

    suspend fun deleteAlert (alertItem : AlarmItem) : Int
    {
        return dao.deleteAlarm(alertItem)
    }

    fun getAllAlarms () : Flow<List<AlarmItem>>{

        return dao.getAllAlarms()
    }


}