package com.example.theweather

import com.example.theweather.db.IFavouriteLocationsLocalDataSource
import com.example.theweather.model.FavouriteLocationItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeLocalDataSource (private var localFavDataSourceList : MutableList<FavouriteLocationItem> = mutableListOf() )
    : IFavouriteLocationsLocalDataSource{
    override suspend fun insert(favItem: FavouriteLocationItem): Long {

        if (localFavDataSourceList.add(favItem))
        {
            return 1}
        else {
            return 0 }
    }

    override suspend fun delete(favItem: FavouriteLocationItem): Int {
        if (localFavDataSourceList.remove(favItem))
        {
            return 1
        }
        else
        {
            return 0
        }


    }

    override fun getAllFavouriteProduct(): Flow<List<FavouriteLocationItem>> {
        return flowOf(localFavDataSourceList)
    }
}