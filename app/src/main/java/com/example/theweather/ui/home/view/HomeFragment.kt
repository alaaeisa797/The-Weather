package com.example.theweather.ui.home.view

import android.Manifest
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.theweather.ApiState
import com.example.theweather.MyConstants

import com.example.theweather.databinding.FragmentHomeBinding
import com.example.theweather.db.FavouriteLocationsLocalDataSource
import com.example.theweather.db.RoomDataBase
import com.example.theweather.model.CurrentDayWeatherResponce
import com.example.theweather.model.ForecastItem
import com.example.theweather.model.ForecastResponse
import com.example.theweather.model.Reposatory
import com.example.theweather.network.NetWorkService
import com.example.theweather.network.RemoteDataSource
import com.example.theweather.network.RetrofitHelper
import com.example.theweather.ui.home.view_model.HomeViewModel
import com.example.theweather.ui.home.view_model.HomeViewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class HomeFragment : Fragment() {


    lateinit var binding: FragmentHomeBinding
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var hourlyTempAdapter: HourlyTempAdapter
    lateinit var weekluTempAdapter: WeeklyTempAdapter
    lateinit var sharedPreferences: SharedPreferences
    lateinit var language: String
    lateinit var unit: String
    lateinit var windSpeed: String
    var minTemp = Double.MAX_VALUE
    var maxTemp = Double.MIN_VALUE


    val permissionId = 1


    lateinit var homeViewModel: HomeViewModel
    lateinit var vmFactory: HomeViewModelFactory


    override fun onStart() {
        super.onStart()


       // language= "ar"
        sharedPreferences = requireActivity().getSharedPreferences(MyConstants.MY_SHARED_PREFERANCE,Context.MODE_PRIVATE)

       language = sharedPreferences.getString(MyConstants.MY_LANGYAGE_API_KEY,"en")?:"en"
        unit = sharedPreferences.getString(MyConstants.MY_TEMP_UNIT,"Celsius")?:"Celsius"
        windSpeed = sharedPreferences.getString(MyConstants.MY_WIND_SPEED,"Meter/Sec")?:"Meter/Sec"
//            val indecator: String = HomeFragmentArgs.fromBundle(arguments).indecator
//
////
////                // Extract latitude and longitude from arguments safely
//                val latitude: Double = HomeFragmentArgs.fromBundle(arguments).latitude.toDouble()
//                val longitude: Double = HomeFragmentArgs.fromBundle(arguments).logitude.toDouble()
////
//        Log.d("TAG", "onStart: indecator $indecator ")
//        Log.d("TAG", "onStart:  latitude $latitude")
//        Log.d("TAG", "onStart:  longtude $longitude")

        val argument: MutableList<String> = MutableList(3) { "" }

        if (!requireArguments().isEmpty) {
            val myArgument = HomeFragmentArgs.fromBundle(requireArguments()).myFullLocationInfo.split(",")
// check 3al size 3shan ana bb3at null k default VALUE
            if (myArgument.size ==3) {
                argument[0] = myArgument[0]
                argument[1] = myArgument[1]
                argument[2] = myArgument[2]
            } else {
                Log.e("TAG", "Invalid latLong format, expected 3 parts but got ${myArgument.size}")
            }
        }
        val indecator = argument[0]
        val latitude = argument[1]
        val longitude = argument[2]


        when (indecator) {
            // lw ana gay lel home mn el favFragment >> m3aya args ya3ni
            "FavLocation" -> { getSpecificLocation(latitude.toDouble(), longitude.toDouble(),language) }
// case enni lessa bade2 el app
            else -> {
                if (checkSelfPermission()) {
                    Log.d("TAG", "onStart: checkSelfPermission")
                    if (isLocationEnabled()) {
                        getFreshLocation()
                    } else {
                        enableLocationServices()
                    }
                } else {
                    Log.d("TAG", "onStart: requestPermission")
                    requestPermission()
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //initalize my ViewModel
        vmFactory = HomeViewModelFactory(
            Reposatory.getInstance(
                RemoteDataSource.getInstance(RetrofitHelper.retrofitInstance.create(NetWorkService::class.java)),
                FavouriteLocationsLocalDataSource(
                    RoomDataBase.getInstance(requireContext()).getAllFavLoacations()
                )
            )
        )
        homeViewModel = ViewModelProvider(this, vmFactory).get(HomeViewModel::class.java)
//////////////////////////////////////////////////////////////
        hourlyTempAdapter = HourlyTempAdapter()
        binding.rvHourlyTemp.apply {

            adapter = hourlyTempAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
/////////////////////////////////////////////////////////////
        weekluTempAdapter = WeeklyTempAdapter()
        binding.rvWeeklyforecast.apply {
            adapter = weekluTempAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        observOnCurrentDayForecast()
        onserveOnDailyForecast()


    }
    fun onserveOnDailyForecast ()
    {

        lifecycleScope.launch {
            homeViewModel.liveDataForFiveDays.collectLatest { result ->
                when (result) {
                    is ApiState.Failure -> Log.d(
                        "TAG",
                        "onCreateView: Faliure case   in the5 dayes forecast "
                    )

                    is ApiState.Loading -> Log.d(
                        "TAG",
                        "onCreateView: Loading case in the5 dayes forecast "
                    )

                    is ApiState.Success -> {

                        Log.d("TAG", "onCreateView: da el weather  ${result.data.list}")

                        val myResult = result.data
                        setHourlyForeCast(myResult)
                        setDailyMaxAndMinTemp(myResult)


                    }
                }
            }
        }
    }

    fun observOnCurrentDayForecast ()
    {
        lifecycleScope.launch {
            homeViewModel.liveData.collect { result ->
                when (result) {
                    is ApiState.Loading -> {

                        Log.d("TAG", "onCreateView: Loading case ")
                    }

                    is ApiState.Success -> {
                        val myResult = result.data
                        setCurrentWeatherDataAndCondition(myResult)

                    }

                    else -> {

                        Log.d("TAG", "onCreateView: Faliure case ")
                    }
                }
            }
        }
    }

    fun setDailyMaxAndMinTemp (result : ForecastResponse)
    {
        val setOfDate /*to ensure uniqnesses*/ = mutableSetOf<String>()
        val ListOfMaxAndMinTempEachDay = mutableListOf<ForecastItem>()
        result.list.forEach {
            if (setOfDate.add(it.dt_txt.split(" ")[0])/*to get date not timen*/) {
                ListOfMaxAndMinTempEachDay.add(it)
                minTemp = Double.MAX_VALUE
                maxTemp = Double.MIN_VALUE

            }
            if (it.main.temp_max > maxTemp) maxTemp = it.main.temp_max
            if (it.main.temp_min < minTemp) minTemp = it.main.temp_min
            ListOfMaxAndMinTempEachDay.get(ListOfMaxAndMinTempEachDay.size - 1).main.temp_max = maxTemp
            ListOfMaxAndMinTempEachDay.get(ListOfMaxAndMinTempEachDay.size - 1).main.temp_min = minTemp

        }
        //Submit to my adapter of weekly

        weekluTempAdapter.submitList(ListOfMaxAndMinTempEachDay)

    }

    fun setHourlyForeCast (result : ForecastResponse)
    {
        // today date
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        //  tomorrow date
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        val tomorrow = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
        // Filter items by today's date and tomorrow's date
        val Alist = result.list.filter { forecastItem ->
            // Parse the dt_txt to a Date object
            val itemDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(forecastItem.dt_txt)
            // Format the parsed date to yyyy-MM-dd format
            val formattedItemDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(itemDate)
            // Compare with today's date and tomorrow's date
            formattedItemDate == today || formattedItemDate == tomorrow
        }

        // Submit to my adapter of horuly
        hourlyTempAdapter.submitList(Alist)

    }
    fun setCurrentWeatherDataAndCondition(result :CurrentDayWeatherResponce)
    {

        when (language)
        {
            "ar"->{
                when (windSpeed)
                {
                    "Mile/Hour"->{
                        binding.tvWindSpeed.text = "${convertMeterPerSecToMilePerHour(result.wind.speed.toDouble())} ميل/ساعه "
                    }
                    else->{binding.tvWindSpeed.text = "${result.wind.speed} م/ث"}
                }
                // card 1
                binding.tvTodayDisc.text = result.weather.get(0).description
                val readable_location =
                    getReadableLocation(result.coord.lat, result.coord.lon)
                binding.tvLocation.text = readable_location


        binding.tvPressure.text = "${result.main.pressure} ََض ج"
        binding.tvHumidity.text = "${result.main.humidity} %"

        binding.tvCloud.text = "${result.clouds.all} %"
        binding.tvFeelsLike.text = "${result.main.feels_like} ٍس "
        binding.tvVisability.text = "${result.visibility} م "

                when (unit )
                {
                    "Fahrenheit"->{
                        binding.tvFeelsLike.text= "${convertFromCelsiusToFahrenheit(result.main.feels_like).toString()}ف°"
                        binding.tvTodayTemperature.text = "${convertFromCelsiusToFahrenheit(result.main.temp)}ف° "
                    }
                    "Kelvin"->{
                        binding.tvFeelsLike.text= "${convertFromCelsiusToKelvin(result.main.feels_like).toString()}ك°"
                        binding.tvTodayTemperature.text = "${convertFromCelsiusToKelvin(result.main.temp)}ك° "
                    }
                    else ->{
                        binding.tvFeelsLike.text = "${result.main.feels_like} س°"
                        binding.tvTodayTemperature.text = "${result.main.temp}س° "
                    }
                }

            }
                else -> // case language english
                    {
                        when (windSpeed)
                        {
                            "Mile/Hour"->{

                                binding.tvWindSpeed.text = "${convertMeterPerSecToMilePerHour(result.wind.speed.toDouble())} mile/h"
                            }
                            else->{ binding.tvWindSpeed.text = "${result.wind.speed} m/sec"}
                        }
                        // card 1

                        binding.tvTodayDisc.text = result.weather.get(0).description
                        val readable_location =
                            getReadableLocation(result.coord.lat, result.coord.lon)
                        binding.tvLocation.text = readable_location
                        // Log.d("TAG", "onCreateView: el readable location ${readable_location?.get(0)?.getAddressLine(0).toString()}")

                        //card 2
                        binding.tvPressure.text = "${result.main.pressure} hpa"
                        binding.tvHumidity.text = "${result.main.humidity} %"


                        binding.tvCloud.text = "${result.clouds.all} %"
                        binding.tvVisability.text = "${result.visibility} m"

                        // now displaying the units
                        when (unit ){
                            "Fahrenheit"->{
                                binding.tvFeelsLike.text= "${convertFromCelsiusToFahrenheit(result.main.feels_like).toString()}°F"
                                binding.tvTodayTemperature.text = "${convertFromCelsiusToFahrenheit(result.main.temp)}°F "
                            }
                            "Kelvin"->{
                                binding.tvFeelsLike.text= "${convertFromCelsiusToKelvin(result.main.feels_like).toString()}°K"
                                binding.tvTodayTemperature.text = "${convertFromCelsiusToKelvin(result.main.temp)}°K "
                            }
                            else ->{
                                binding.tvFeelsLike.text = "${result.main.feels_like} °C"
                                binding.tvTodayTemperature.text = "${result.main.temp}°C "
                            }
                        }

                    }
        }


        Log.d("TAG", "onCreateView: Success case ")
        Log.d("TAG", "onCreateView: da el weather  ${result.weather} ")
        Log.d("TAG", "onCreateView: da el weather size   ${result.weather.size} ")

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    fun getSpecificLocation(lat: Double, long: Double,language: String) {
        homeViewModel.getfiveForecast(lat, long,language)
        homeViewModel.getWitherOfTheDay(lat, long,language)

    }

    @SuppressLint("MissingPermission")
    fun getFreshLocation() {

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())
        fusedLocationProviderClient.requestLocationUpdates(
            LocationRequest.Builder(120 * 1000).apply {
                setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            }.build(),
            object : LocationCallback() {
                override fun onLocationResult(p0: LocationResult) {
                    super.onLocationResult(p0)

                    Log.d("TAG", "onLocationResult: ${p0.locations.toString()}")

                    val long = p0.locations.lastOrNull()?.longitude
                    val lat = p0.locations.lastOrNull()?.latitude

                    if (lat != null && long != null) {
                        homeViewModel.getWitherOfTheDay(lat, long , language )
                    }
                    if (lat != null && long != null) {
                        homeViewModel.getfiveForecast(lat, long , language )
                    }

                    binding.tvLocation.text = p0.lastLocation?.longitude.toString()
                    Log.d("TAG", "onLocationResult: ${p0.lastLocation?.longitude.toString()}")
                }
            },
            Looper.myLooper()
        )
    }

    fun checkSelfPermission(): Boolean {
        var temp = false
        if ((ActivityCompat.checkSelfPermission(
                requireContext(),
                ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED)
            || (ActivityCompat.checkSelfPermission(
                requireContext(),
                ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED)
        ) {
            temp = true
        }
        return temp
    }

    fun requestPermission() {

        requestPermissions(

            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ), permissionId
        )
    }

    fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    fun enableLocationServices() {
        val intent: Intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == permissionId) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED || grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                getFreshLocation()
            }
        }
    }

    fun getReadableLocation(latitude: Double, longitude: Double): String {
        val geocoder = Geocoder(requireContext())
        val readableLocation = geocoder.getFromLocation(latitude, longitude, 1)
        val address = readableLocation?.get(0)

        // Log all available components for debugging
        Log.d("TAG", "Full Address: ${address?.getAddressLine(0)}")
        Log.d("TAG", "SubLocality: ${address?.subLocality}")
        Log.d("TAG", "Locality: ${address?.locality}")
        Log.d("TAG", "Admin Area: ${address?.adminArea}")
        Log.d("TAG", "Country: ${address?.countryName}")

        val city = address?.subLocality ?: address?.locality ?: "Unknown City"
        val state = address?.adminArea ?: "Unknown State"
        val country = address?.countryName ?: "Unknown Country"

        return "$city, $state, $country"
    }
    fun convertFromCelsiusToFahrenheit(temp :Double) :Int
    {
        return ((temp * 1.8) + 32).toInt()
    }
    fun convertFromCelsiusToKelvin (temp :Double) :Int
    {
       return (temp+273.15).toInt()
    }

    fun convertMeterPerSecToMilePerHour (m : Double ):Int
    {
        return (m*2.23694).toInt()
    }

}