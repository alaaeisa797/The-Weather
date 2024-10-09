package com.example.theweather.model

import com.example.theweather.FakeLocalDataSource
import com.example.theweather.FakeRemoteDataSource
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test


class ReposatoryTest {

    private lateinit var fakeRemoteDataSource: FakeRemoteDataSource
    private lateinit var fakeLocalDataSource: FakeLocalDataSource
    private lateinit var repo : Reposatory
    val currentWeather = CurrentDayWeatherResponce(
        id=501,
        coord = Coord(10.99, 44.34),
       weather =  listOf(Weather( description = "moderate rain", "10d")),
       base =  "stations",
        main=MainWeather(285.95, 285.74, 284.94, 287.76, 1009, 94, 1009, 942),
        visibility = 8412,
         wind= Wind(2.87, 167, 5.81),
        clouds= Clouds(100),
        dt = 1728380785,
        sys= Sys(2, 2044440, "IT", 1728364943, 1728405860),
        timezone = 7200,
       name =  "Zocca",
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
    fun SetUp ()
    {
        fakeLocalDataSource = FakeLocalDataSource()
        fakeRemoteDataSource = FakeRemoteDataSource(currentWeather,weatherResponse)
        repo = Reposatory.getInstance(fakeRemoteDataSource, fakeLocalDataSource)
    }

    @Test
    fun getCurrentWeatherOfDay_LongAndLat_CurrentDayWeatherResponce ()= runBlocking {
        //when

        var result = repo.getWitherOfTheDay(0.0,0.0,"en") .first()
//then
        assertEquals(currentWeather , result)

    }
    @Test
    fun InsertFAvLocation_FavouriteLocationItem_trueOrFalse () = runBlocking {
        val favLocationItem=FavouriteLocationItem(
            id = 500,
            address ="ismailia" ,
         lat= 10.111111,
         lng =-100.55

        )
        repo.insertToFavourie(favLocationItem)

        val allFavorites = repo.getAllFavouriteLocations ().first()

        if (allFavorites != null) {
            assertTrue(allFavorites.contains(favLocationItem))
        }

    }





}