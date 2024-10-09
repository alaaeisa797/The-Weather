package com.example.theweather.ui.home.view_model

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.theweather.FakeLocalDataSource
import com.example.theweather.FakeRemoteDataSource
import com.example.theweather.FakeReposatory
import com.example.theweather.model.Clouds
import com.example.theweather.model.Coord
import com.example.theweather.model.CurrentDayWeatherResponce
import com.example.theweather.model.ForecastItem
import com.example.theweather.model.ForecastResponse
import com.example.theweather.model.MainWeather
import com.example.theweather.model.Sys
import com.example.theweather.model.Weather
import com.example.theweather.model.Wind

import kotlinx.coroutines.test.runBlockingTest


import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import kotlinx.coroutines.runBlocking



@RunWith(AndroidJUnit4::class)
class HomeViewModelTest {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var fakeReposatory: FakeReposatory

    val currentWeather = CurrentDayWeatherResponce(
        id=501,
        coord = Coord(10.99, 44.34),
        weather =  listOf(Weather( description = "moderate rain", "10d")),
        base =  "stations",
        main= MainWeather(285.95, 285.74, 284.94, 287.76, 1009, 94, 1009, 942),
        visibility = 8412,
        wind= Wind(2.87, 167, 5.81),
        clouds= Clouds(100),
        dt = 1728380785,
        sys= Sys(2, 2044440, "IT", 1728364943, 1728405860),
        timezone = 7200,
        name =  "mmm",
        cod = 200
    )

    var weatherResponse = ForecastResponse(

        list = listOf(
            ForecastItem(
                main = MainWeather(
                    temp = 20.5,
                    feels_like = 19.8,
                    temp_min = 18.0,
                    temp_max = 23.0,
                    pressure = 1012,
                    humidity = 85,
                    sea_level = 1012,
                    grnd_level = 1000
                ),
                weather = listOf(
                    Weather(

                        description = "clear sky",
                        icon = "01d"
                    )
                ),
                dt_txt = "2024-10-08 12:00:00"
            )
        )
    )

    @Before
    fun SetUp()
    {
        fakeReposatory= FakeReposatory(FakeRemoteDataSource(currentWeather,weatherResponse),FakeLocalDataSource())
        homeViewModel= HomeViewModel(fakeReposatory)
    }

    @Test
    fun getCurrentWeather_Succes ()= runBlocking {
        // Given
        val latitude = 0.0
        val longitude = 0.0
        val language = "en"
        // When
        val result = homeViewModel.getWitherOfTheDay(latitude, longitude, language)
        // Then
        assertThat(result, not(nullValue())) 
    }
    @Test
    fun getForecast_Succes () = runBlocking {
        val latitude = 0.0
        val longitude = 0.0
        val language = "en"
        val result = homeViewModel.getfiveForecast(latitude,longitude,language)

        assertThat(result, not(nullValue()))

    }




}