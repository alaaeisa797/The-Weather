package com.example.theweather.ui.alarm.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.theweather.favourite.view_model.FavouriteViewModel
import com.example.theweather.model.IReposatory
import com.example.theweather.model.Reposatory

class AlarmViewModelFactory (private val repo : IReposatory) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return return if (modelClass.isAssignableFrom(AlarmViewModel::class.java))
        {
            AlarmViewModel(repo) as T
        }
        else
        {
            throw IllegalArgumentException(" class favourite view  model is not found!")
        }
    }
}