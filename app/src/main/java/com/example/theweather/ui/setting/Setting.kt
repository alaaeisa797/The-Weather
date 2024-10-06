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
import com.example.theweather.MyConstants

import com.example.theweather.databinding.FragmentSettingBinding

class Setting : Fragment() {

    private var _binding: FragmentSettingBinding? = null

    private val binding get() = _binding!!
    lateinit var sharedPreferences: SharedPreferences

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

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val language = sharedPreferences.getString(MyConstants.MY_LANGYAGE_API_KEY,"en")
        val unit = sharedPreferences.getString(MyConstants.MY_TEMP_UNIT,"Celsius")
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
        binding.rgLanguages.setOnCheckedChangeListener { groub, id ->

            val languageRadioButton: RadioButton = binding.root.findViewById(id) as RadioButton
            when (languageRadioButton.text) {
                "Arabic" -> {
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
                "Kelvin"->{
                    sharedPreferences.edit().putString(MyConstants.MY_TEMP_UNIT, "Kelvin").apply()
                    Thread.sleep(1000)
                    restartApp()
                }
                "Fahrenheit"->{
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



}