package com.example.theweather.db


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

}