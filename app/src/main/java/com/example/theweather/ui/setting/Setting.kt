package com.example.theweather.ui.setting

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.theweather.MyConstants

import com.example.theweather.databinding.FragmentSettingBinding

class Setting : Fragment() {

    private var _binding: FragmentSettingBinding? = null

    private val binding get() = _binding!!
    lateinit var sharedPreferences: SharedPreferences
    lateinit var SharedPrefToDetectdWayOfLocationing: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val galleryViewModel = ViewModelProvider(this).get(SettingViewModel::class.java)

        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // creating
        sharedPreferences =
            requireActivity().getSharedPreferences(MyConstants.MY_SHARED_PREFERANCE, Context.MODE_PRIVATE)
        SharedPrefToDetectdWayOfLocationing= requireActivity().getSharedPreferences("MapOrGpsIsClicked", Context.MODE_PRIVATE)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val language = sharedPreferences.getString(MyConstants.MY_LANGYAGE_API_KEY,"en")
        val unit = sharedPreferences.getString(MyConstants.MY_TEMP_UNIT,"Celsius")
        val windSpeed = sharedPreferences.getString(MyConstants.MY_WIND_SPEED,"Meter/Sec")
        val locationWay = sharedPreferences.getString(MyConstants.MY_LOCATION_WAY,"GPS")
        val mapOrGps = SharedPrefToDetectdWayOfLocationing.getString("map||gps","gpsIsClicked")
        when(language)
        {
            "ar"-> binding.rbArabic.isChecked = true
            "en"-> binding.rbEnglish.isChecked = true
        }
        when(unit)
        {
            "Kelvin"-> binding.rbKelvin.isChecked = true
            "Fahrenheit"-> binding.rbFehrenhayt.isChecked = true
            "Celsius"-> binding.rbCelicious.isChecked = true
        }
        when(windSpeed)
        {
           "Meter/Sec" ->binding.rbMeterSec.isChecked = true
           "Mile/Hour" ->binding.rbMileHour.isChecked = true
        }
//        when(mapOrGps)
//        {
//            "gpsIsClicked"-> binding.rbGps.isChecked = true
//            "mapIsClicked"->binding.rbMap.isChecked= true
//
//        }
        binding.rgLanguages.setOnCheckedChangeListener { groub, id ->

            val languageRadioButton: RadioButton = binding.root.findViewById(id) as RadioButton
            when (languageRadioButton.text) {
                "عربي","Arabic" -> {
                    sharedPreferences.edit().putString(MyConstants.MY_LANGYAGE_API_KEY, "ar").apply()
                    Thread.sleep(1000)
                    changeAppLanguage("ar")
                    restartApp()
                }
                else->{
                    sharedPreferences.edit().putString(MyConstants.MY_LANGYAGE_API_KEY, "en").apply()
                    Thread.sleep(1000)
                    changeAppLanguage("en")
                    restartApp()
                }
            }


        }

        binding.rgTemperature.setOnCheckedChangeListener{ groub, id->
            val temperatuerRadioButton: RadioButton = binding.root.findViewById(id) as RadioButton
            Log.d("TAG", "onViewCreated: $unit")
            when (temperatuerRadioButton.text)
            {
                "كلفن","Kelvin"->{
                    sharedPreferences.edit().putString(MyConstants.MY_TEMP_UNIT, "Kelvin").apply()
                    Thread.sleep(1000)
                    restartApp()
                }
                "فهرنهايت","Fahrenheit"->{
                    sharedPreferences.edit().putString(MyConstants.MY_TEMP_UNIT, "Fahrenheit").apply()
                    Thread.sleep(1000)
                    restartApp()
                }
                else ->{
                    sharedPreferences.edit().putString(MyConstants.MY_TEMP_UNIT, "Celsius").apply()
                    Thread.sleep(1000)
                    restartApp()
                }
            }
        }

        binding.rgWindSpeed.setOnCheckedChangeListener{groub, id ->
            val windSpeedRadioButton: RadioButton = binding.root.findViewById(id) as RadioButton
            when (windSpeedRadioButton.text)
            {
                "Mile/Hour","ميل/ساعه"->{
                    sharedPreferences.edit().putString(MyConstants.MY_WIND_SPEED, "Mile/Hour").apply()
                    Thread.sleep(1000)
                    restartApp()
                }
                else->{
                    sharedPreferences.edit().putString(MyConstants.MY_WIND_SPEED, "Meter/Sec").apply()
                    Thread.sleep(1000)
                    restartApp()
                }
            }

        }

        binding.rgLocations.setOnCheckedChangeListener{groub, id->
            val LocationRadioButton: RadioButton = binding.root.findViewById(id) as RadioButton
            when (LocationRadioButton.text)
            {
                "خريطه","Map"->{
                        // hena ha2olo ro7 el map
                    sharedPreferences.edit().putString(MyConstants.MY_LOCATION_WAY, "Map").apply()
                    SharedPrefToDetectdWayOfLocationing.edit().putString("map||gps","gpsIsClicked").apply()
                    val action = SettingDirections.actionNavSettingToSettingMapFragment()
                   Navigation.findNavController(binding.root).navigate(action)
                }
                else->{
                    sharedPreferences.edit().putString(MyConstants.MY_LOCATION_WAY, "GPS").apply()
                    SharedPrefToDetectdWayOfLocationing.edit().putString("map||gps","mapIsClicked").apply()
                   val action = SettingDirections.actionNavSettingToNavHome()
                    Navigation.findNavController(binding.root).navigate(action)
                }
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun changeAppLanguage(language: String) {
        val languageCode = when (language) {
            "ar" -> "arabic"
            "en" -> "english"
            else -> "english"
        }
        sharedPreferences.edit().putString(MyConstants.MY_LANGYAGE_APP_KEY,languageCode).apply()
        val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(language)
        AppCompatDelegate.setApplicationLocales(appLocale)
    }

    private fun restartApp() {
        activity?.let {
            val intent = it.intent
            it.finish()
            startActivity(intent)
        }
    }

    fun restartFragment(fragment: Fragment) {
        val fragmentManager = fragment.parentFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.detach(fragment)
        fragmentTransaction.attach(fragment)
        fragmentTransaction.commit()
    }



}