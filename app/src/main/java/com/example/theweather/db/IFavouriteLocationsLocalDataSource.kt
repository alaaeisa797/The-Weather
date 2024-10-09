package com.example.theweather.db

import com.example.theweather.model.FavouriteLocationItem
import kotlinx.coroutines.flow.Flow

interface IFavouriteLocationsLocalDataSource {
    suspend fun insert(favItem: FavouriteLocationItem): Long

    suspend fun delete(favItem: FavouriteLocationItem): Int
    fun getAllFavouriteProduct () : Flow<List<FavouriteLocationItem>>
}