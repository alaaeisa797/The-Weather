package com.example.theweather.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

data class CurrentDayWeatherResponce( val coord: Coord,
                                      val weather: List<Weather>,
                                      val base: String,
                                      val main: MainWeather,
                                      val visibility: Int,
                                      val wind: Wind,
                                      val clouds: Clouds,
                                      val dt: Long,
                                      val sys: Sys,
                                      val timezone: Int,
                                      val id: Long,
                                      val name: String,
                                      val cod: Int
)

data class MainWeather(
    val temp: Double,
    val feels_like: Double,
    var temp_min: Double,
    var temp_max: Double,
    val pressure: Int,
    val humidity: Int,
    val sea_level: Int,
    val grnd_level: Int
)
data class Coord(
    val lon: Double,
    val lat: Double
)
data class Wind(
    val speed: Double,
    val deg: Int,
    val gust: Double
)

data class Clouds(
    val all: Int
)

data class Sys(
    val type: Int,
    val id: Int,
    val country: String,
    val sunrise: Long,
    val sunset: Long
)

data class Weather(val description: String, val icon: String) // describition of weather condition and icon to chech what image to display

data class ForecastResponse(val list : List<ForecastItem>)

data class ForecastItem(val main: MainWeather, val weather: List<Weather>, val dt_txt: String/*da el date  */)


// lazem t8yar el primary key da 3shan mt5znsh nfs el 3nwan mrten
@Entity(tableName = "MyFavouriteItems")
data class FavouriteLocationItem(
    @PrimaryKey(autoGenerate = true)
    var id:Int =0,
    val address:String ,
    val lat: Double,
    val lng :Double
)