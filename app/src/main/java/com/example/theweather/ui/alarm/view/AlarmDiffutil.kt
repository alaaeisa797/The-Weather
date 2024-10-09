package com.example.theweather.ui.alarm.view

import androidx.recyclerview.widget.DiffUtil
import com.example.theweather.model.AlarmItem
import com.example.theweather.model.ForecastItem

class AlarmDiffutil  : DiffUtil.ItemCallback<AlarmItem>() {




    override fun areItemsTheSame(oldItem: AlarmItem, newItem: AlarmItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: AlarmItem, newItem: AlarmItem): Boolean {
        return oldItem == newItem
    }
}
