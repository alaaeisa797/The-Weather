package com.example.theweather.db


import com.example.theweather.model.FavouriteLocationItem
import kotlinx.coroutines.flow.Flow

class FavouriteLocationsLocalDataSource (val dao : DAO) {

    suspend fun insert (favItem :FavouriteLocationItem) :Long
    {
       return dao.insert(favItem)
    }

    suspend fun delete (favItem :FavouriteLocationItem) : Int
    {
       return dao.delete(favItem)
    }
     fun getAllFavouriteProduct () : Flow<List<FavouriteLocationItem>>
    {
        return  dao.getAllMyFavouriteLocations()
    }
}