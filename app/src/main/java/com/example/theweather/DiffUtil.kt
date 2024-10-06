package com.example.theweather

import androidx.recyclerview.widget.DiffUtil
import com.example.theweather.model.ForecastItem
import com.example.theweather.model.ForecastResponse

class DiffUtil : DiffUtil.ItemCallback<ForecastItem>() {
    override fun areItemsTheSame(oldItem: ForecastItem, newItem: ForecastItem): Boolean {
        return oldItem.dt_txt == newItem.dt_txt
    }

    override fun areContentsTheSame(oldItem: ForecastItem, newItem: ForecastItem): Boolean {
        return oldItem == newItem
    }
}