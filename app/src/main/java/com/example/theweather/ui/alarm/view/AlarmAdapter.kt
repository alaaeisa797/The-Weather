package com.example.theweather.ui.alarm.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.theweather.MyConstants

import com.example.theweather.databinding.AlarmSingleItemBinding
import com.example.theweather.databinding.CurrentDayHourItemBinding
import com.example.theweather.favourite.view.OnClickListner
import com.example.theweather.model.AlarmItem
import com.example.theweather.model.FavouriteLocationItem
import com.example.theweather.ui.home.view.HourlyTempAdapter

class AlarmAdapter (var  onClick : OnClickListner<AlarmItem>) : ListAdapter<AlarmItem, AlarmAdapter.AlarmViewHolder>(AlarmDiffutil()) {

    lateinit var language : String
    lateinit var sharedPreferences: SharedPreferences
    lateinit var binding: AlarmSingleItemBinding
    class AlarmViewHolder ( val binding : AlarmSingleItemBinding) : RecyclerView.ViewHolder(binding.root){}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        val layoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = AlarmSingleItemBinding.inflate(layoutInflater)
        sharedPreferences = parent.context.getSharedPreferences(
            MyConstants.MY_SHARED_PREFERANCE,
            Context.MODE_PRIVATE)
         language = sharedPreferences.getString(MyConstants.MY_LANGYAGE_API_KEY,"en")?:"en"
        return AlarmAdapter.AlarmViewHolder(binding)
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
      val   currentAlarm =getItem(position)

      holder.binding.tvAlarmCityAndTime.text = currentAlarm.address




        holder.binding.cvAlarm.setOnClickListener{
            onClick.OnClick(currentAlarm)
        }

    }



}