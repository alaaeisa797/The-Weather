package com.example.theweather.ui.home.view_model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.theweather.ApiState
import com.example.theweather.model.CurrentDayWeatherResponce
import com.example.theweather.model.ForecastResponse
import com.example.theweather.model.Reposatory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class HomeViewModel (val repo : Reposatory) : ViewModel() {

    private val mutableLiveData = MutableStateFlow<ApiState<CurrentDayWeatherResponce>>(ApiState.Loading())
    val liveData = mutableLiveData.asStateFlow()

    private val mutableLiveDataForFiveDays = MutableStateFlow<ApiState<ForecastResponse>>(ApiState.Loading())
    val liveDataForFiveDays = mutableLiveDataForFiveDays.asStateFlow()

    fun getWitherOfTheDay(lat:Double,long:Double , language : String)
    {
        viewModelScope.launch(Dispatchers.IO) {
            repo.getWitherOfTheDay(lat,long,language)
                .catch {
                    mutableLiveData.value = ApiState.Failure(it.toString())
                }.collect{
                    mutableLiveData.value = ApiState.Success(it)
                }
        }
    }

    fun getfiveForecast (lat:Double,long:Double , language : String)
    {
        viewModelScope.launch(Dispatchers.IO) {
            repo.getFiveDaysForecast(lat,long,language)
                .catch {
                    mutableLiveDataForFiveDays.value = ApiState.Failure(it.toString())
                    Log.d("TAG", "getfiveForecast: ${it.printStackTrace()}")
                }.collect{

                    mutableLiveDataForFiveDays.value = ApiState.Success(it)


                }
        }
        }


}