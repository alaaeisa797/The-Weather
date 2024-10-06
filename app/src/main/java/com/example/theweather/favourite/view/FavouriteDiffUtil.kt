package com.example.theweather.favourite.view

import androidx.recyclerview.widget.DiffUtil
import com.example.theweather.model.FavouriteLocationItem

class FavouriteDiffUtil : DiffUtil.ItemCallback<FavouriteLocationItem>() {
    override fun areItemsTheSame(
        oldItem: FavouriteLocationItem,
        newItem: FavouriteLocationItem
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: FavouriteLocationItem,
        newItem: FavouriteLocationItem
    ): Boolean {

        return oldItem == newItem
    }
}