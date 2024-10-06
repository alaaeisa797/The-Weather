package com.example.theweather.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.theweather.model.FavouriteLocationItem
import kotlinx.coroutines.flow.Flow

@Dao
interface DAO {

    @Query("SELECT * FROM  MyFavouriteItems")
    fun getAllMyFavouriteLocations  () : Flow<List<FavouriteLocationItem>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert  (favourteItem : FavouriteLocationItem) :  Long

    @Delete
    suspend fun delete (favourteItem : FavouriteLocationItem) : Int
}