package com.example.theweather.ui.home.view

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.theweather.DiffUtil
import com.example.theweather.MyConstants
import com.example.theweather.databinding.CurrentDayHourItemBinding
import com.example.theweather.model.ForecastItem
import java.text.SimpleDateFormat
import java.util.Locale

class HourlyTempAdapter :ListAdapter<ForecastItem,HourlyTempAdapter.HourlyTempViewHolder>(DiffUtil())  {
      lateinit var binding : CurrentDayHourItemBinding
    lateinit var sharedPreferences: SharedPreferences
    lateinit var unit : String
    lateinit var language : String



    class HourlyTempViewHolder ( val binding : CurrentDayHourItemBinding) : RecyclerView.ViewHolder(binding.root){}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyTempViewHolder {

        val layoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = CurrentDayHourItemBinding.inflate(layoutInflater)
        sharedPreferences = parent.context.getSharedPreferences(MyConstants.MY_SHARED_PREFERANCE,Context.MODE_PRIVATE)
        unit = sharedPreferences.getString(MyConstants.MY_TEMP_UNIT,"Celsius")?:"Celsius"
        language = sharedPreferences.getString(MyConstants.MY_LANGYAGE_API_KEY,"en")?:"en"
        return HourlyTempViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HourlyTempViewHolder, position: Int) {
        val currentHourlyTempAdapter = getItem(position)

        when (language)
        {
            "ar"->{
                when (unit )
                {
                    "Fahrenheit"->{
                        holder.binding .tvHourlyTemp .text = "${convertFromCelsiusToFahrenheit(currentHourlyTempAdapter.main.temp)} ف° "
                    }
                    "Kelvin"->{
                        holder.binding .tvHourlyTemp .text = "${convertFromCelsiusToKelvin(currentHourlyTempAdapter.main.temp)}  ك°"
                    }
                    else->{
                        holder.binding .tvHourlyTemp .text = "${currentHourlyTempAdapter.main.temp.toString()} س°"
                    }
                }
            }
            else->{
                when (unit )
                {
                    "Fahrenheit"->{
                        holder.binding .tvHourlyTemp .text = "${convertFromCelsiusToFahrenheit(currentHourlyTempAdapter.main.temp)} °F"
                    }
                    "Kelvin"->{
                        holder.binding .tvHourlyTemp .text = "${convertFromCelsiusToKelvin(currentHourlyTempAdapter.main.temp)} °k"
                    }
                    else->{
                        holder.binding .tvHourlyTemp .text = "${currentHourlyTempAdapter.main.temp.toString()} °C"
                    }

                }
            }


        }

        holder.binding .tvHourly.text =  convertTo12HourTime(currentHourlyTempAdapter.dt_txt.toString())
       // heya de  holder.binding .tvHourlyTemp .text = currentHourlyTempAdapter.main.temp.toString()

        val conditionImageUrl = "https://openweathermap.org/img/wn/${currentHourlyTempAdapter.weather[0].icon}.png"
        Glide.with(holder.itemView.context)
            .load(conditionImageUrl)
            .into(holder.binding.ivTempPic)


    }

    fun convertTo12HourTime(dateTime: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("hh a", Locale.getDefault())
        val date = inputFormat.parse(dateTime)
        return outputFormat.format(date!!)
    }
    fun convertFromCelsiusToFahrenheit(temp :Double) :Int
    {
        return ((temp * 1.8) + 32).toInt()
    }
    fun convertFromCelsiusToKelvin (temp :Double) :Int
    {
        return (temp+273.15).toInt()
    }
    fun settempBasedOnUnitAndLanguage(language : String, unit:String )
    {
        when (language)
        {
            "ar"->{
                when(unit)
                {
                    "Fahrenheit"->{

                    }
                    "Kelvin"->{

                    }
                    else->{

                    }
                }
            }
            else->{
                when(unit)
                {
                    "Fahrenheit"->{

                    }
                    "Kelvin"->{

                    }
                    else->{

                    }
                }
            }
        }

    }

}