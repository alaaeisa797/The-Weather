package com.example.theweather.ui.home.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.theweather.model.Reposatory

class HomeViewModelFactory (private val repo :Reposatory ) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return  return if (modelClass.isAssignableFrom(HomeViewModel::class.java))
        {
            HomeViewModel(repo) as T
        }
        else
        {
            throw IllegalArgumentException(" class view  model is not found")
        }
    }
}