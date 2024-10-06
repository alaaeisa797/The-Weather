package com.example.theweather.favourite.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.theweather.ApiState
import com.example.theweather.model.FavouriteLocationItem
import com.example.theweather.model.Reposatory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class FavouriteViewModel (val repo : Reposatory) : ViewModel() {
    private val mutableLocalLiveData =
        MutableStateFlow<ApiState<List<FavouriteLocationItem>>>(ApiState.Loading())
    val localLiveData = mutableLocalLiveData.asStateFlow()

    suspend fun insert(favItem: FavouriteLocationItem): Long {
        return repo.insertToFavourie(favItem)
    }

    fun getFavLoacations() {
        viewModelScope.launch(Dispatchers.IO) {
            repo.getAllFavouriteLocations().catch {

                mutableLocalLiveData.value = ApiState.Failure("unable to  fetch data ")
            }
                .collect {
                    mutableLocalLiveData.value = ApiState.Success(it!!)
                }
        }
    }

    // lessa me7taga tzbeet
    suspend fun delete(favItem: FavouriteLocationItem): Int {
        return repo.deleteFromFavourie(favItem)

    }

}