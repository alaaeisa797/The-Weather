package com.example.theweather.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.theweather.model.AlarmItem
import com.example.theweather.model.FavouriteLocationItem

@Database(entities = arrayOf(FavouriteLocationItem::class,AlarmItem::class), version = 2)
abstract class RoomDataBase : RoomDatabase() {

    abstract fun getAllFavLoacations(): DAO

    companion object {

        private var instance: RoomDataBase? = null

        fun getInstance(context: Context): RoomDataBase {
            return RoomDataBase.instance ?: synchronized(this) {

                val db = Room.databaseBuilder(
                    context.applicationContext,
                    RoomDataBase::class.java,
                    "MyDataBase"
                ).fallbackToDestructiveMigration().build()
                instance = db
                db
            }


        }

    }
}