package com.example.theweather.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object  RetrofitHelper {

    const val URL = "https://api.openweathermap.org/data/2.5/"
    val retrofitInstance = Retrofit.Builder()
        .baseUrl(URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()


}