package com.example.theweather.favourite.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.theweather.model.Reposatory
import com.example.theweather.ui.home.view_model.HomeViewModel

class FavouriteViewModelFactory (private val repo : Reposatory) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return return if (modelClass.isAssignableFrom(FavouriteViewModel::class.java))
        {
            FavouriteViewModel(repo) as T
        }
        else
        {
            throw IllegalArgumentException(" class favourite view  model is not found!")
        }
    }
}