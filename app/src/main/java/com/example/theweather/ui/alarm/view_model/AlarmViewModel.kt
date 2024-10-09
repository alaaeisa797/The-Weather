package com.example.theweather.ui.alarm.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.theweather.ApiState
import com.example.theweather.model.AlarmItem
import com.example.theweather.model.CurrentDayWeatherResponce
import com.example.theweather.model.FavouriteLocationItem
import com.example.theweather.model.IReposatory
import com.example.theweather.model.Reposatory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class AlarmViewModel(val repo : IReposatory) : ViewModel() {

    private val mutableLocalLiveData =
        MutableStateFlow<ApiState<List<AlarmItem>>>(ApiState.Loading())
    val localLiveData = mutableLocalLiveData.asStateFlow()

    suspend fun insertAalrm(favItem: AlarmItem): Long {
      return repo.insertAlarm(favItem)
    }

    fun getAllAlarms() {
        viewModelScope.launch(Dispatchers.IO) {
            repo.getAllAlarms().catch {

                mutableLocalLiveData.value = ApiState.Failure("unable to  fetch data ")
            }
                .collect {
                    mutableLocalLiveData.value = ApiState.Success(it!!)
                }
        }
    }


    suspend fun deleteAlarm(alarmItem: AlarmItem): Int {
        return repo.deleteAlarm (alarmItem)

    }


}