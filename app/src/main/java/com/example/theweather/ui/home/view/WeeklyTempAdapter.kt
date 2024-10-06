package com.example.theweather.ui.home.view

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.theweather.DiffUtil
import com.example.theweather.MyConstants
import com.example.theweather.databinding.TestItemBinding
import com.example.theweather.databinding.WeeklyForecastBinding
import com.example.theweather.model.ForecastItem
import java.text.SimpleDateFormat
import java.util.Locale

class WeeklyTempAdapter : ListAdapter<ForecastItem, WeeklyTempAdapter.WeeklyTempViewHolder>(DiffUtil()) {

    lateinit var binding : TestItemBinding
    lateinit var sharedPreferences: SharedPreferences
    lateinit var unit : String
    lateinit var language : String

    class WeeklyTempViewHolder ( val binding : TestItemBinding) : RecyclerView.ViewHolder(binding.root){}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeeklyTempViewHolder {
        val layoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = TestItemBinding.inflate(layoutInflater)
        sharedPreferences = parent.context.getSharedPreferences(MyConstants.MY_SHARED_PREFERANCE,Context.MODE_PRIVATE)
        unit = sharedPreferences.getString(MyConstants.MY_TEMP_UNIT,"Celsius")?:"Celsius"
        language = sharedPreferences.getString(MyConstants.MY_LANGYAGE_API_KEY,"en")?:"en"

        return WeeklyTempViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WeeklyTempViewHolder, position: Int) {

        val currentForecast = getItem(position)
        var maxTempF=convertFromCelsiusToFahrenheit(currentForecast.main.temp_max)
        var minTempF=convertFromCelsiusToFahrenheit(currentForecast.main.temp_min)

        var maxTempK=convertFromCelsiusToKelvin(currentForecast.main.temp_max)
        var minTempK=convertFromCelsiusToKelvin(currentForecast.main.temp_min)

        var maxTempC=currentForecast.main.temp_max.toInt()
        var minTempC=currentForecast.main.temp_min.toInt()
        when (language)
        {

            "ar"->{
                Log.d("TAG", "onBindViewHolder: ")
                when(unit)
                {
                    "Fahrenheit"->{
                        holder.binding.temperatureMinMax.text= "${maxTempF }/${minTempF} ف° "
                    }
                    "Kelvin"->{
                        holder.binding.temperatureMinMax.text= "${maxTempK }/${minTempK}  ك° "
                    }
                    else->{
                        holder.binding.temperatureMinMax.text= "${maxTempC }/${minTempC} س° "
                    }
                }
            }
            else->{
                when(unit)
                {
                    "Fahrenheit"->{
                        holder.binding.temperatureMinMax.text= "${maxTempF }/${minTempF} °F"

                    }
                    "Kelvin"->{
                        holder.binding.temperatureMinMax.text= "${maxTempK }/${minTempK} °K"
                    }
                    else->{
                        holder.binding.temperatureMinMax.text= "${maxTempC }/${minTempC} °C"
                    }
                }
            }

        }
        holder.binding.day.text = convertFormatToDays(currentForecast.dt_txt)
        holder.binding.weatherDescription.text = currentForecast.weather.get(0).description
          // holder.binding.temperatureMinMax.text = "${currentForecast.main.temp_max}/${currentForecast.main.temp_min}"
          // holder.binding.tvMinT .text = currentForecast.main.temp_min .toString()

        val conditionImageUrl = "https://openweathermap.org/img/wn/${currentForecast.weather[0].icon}.png"
        Glide.with(holder.itemView.context)
            .load(conditionImageUrl)
            .into(holder.binding.weatherIcon)

    }

    fun convertFormatToDays(date:String):String{
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("EEE", Locale.getDefault())
        val date = inputFormat.parse(date)
        return outputFormat.format(date)
    }

    fun convertFromCelsiusToFahrenheit(temp :Double) :Int
    {
        return ((temp * 1.8) + 32).toInt()
    }
    fun convertFromCelsiusToKelvin (temp :Double) :Int
    {
        return (temp+273.15).toInt()
    }
}