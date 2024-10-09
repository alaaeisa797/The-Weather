package com.example.theweather.ui.alarm.view

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.location.Geocoder
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.example.theweather.R
import com.example.theweather.databinding.FragmentMapsAlarmBinding
import com.example.theweather.db.FavouriteLocationsLocalDataSource
import com.example.theweather.db.RoomDataBase
import com.example.theweather.model.AlarmItem
import com.example.theweather.model.Reposatory
import com.example.theweather.network.NetWorkService
import com.example.theweather.network.RemoteDataSource
import com.example.theweather.network.RetrofitHelper
import com.example.theweather.ui.alarm.view_model.AlarmViewModel
import com.example.theweather.ui.alarm.view_model.AlarmViewModelFactory

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale

class MapsAlarmFragment : Fragment() , OnMapReadyCallback {
lateinit var binding : FragmentMapsAlarmBinding
    lateinit var map: GoogleMap
    private var myMarker: Marker? = null
    private lateinit var geocoder: Geocoder
    private var locationAddress: String? = null
    var myAlarm : AlarmItem? = null

    var latitude: Double = 0.0
    var longitude: Double = 0.0


    lateinit var alarmViewModel: AlarmViewModel
    lateinit var vmFactory : AlarmViewModelFactory
    private lateinit var pendingIntent: PendingIntent

    private lateinit var alarmManager: AlarmManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapsAlarmBinding.inflate(inflater,container,false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        geocoder = Geocoder(requireContext(), Locale.getDefault())
        val mapFragment = childFragmentManager.findFragmentById(R.id.maps) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        vmFactory = AlarmViewModelFactory(
            Reposatory.getInstance(
                RemoteDataSource.getInstance(RetrofitHelper.retrofitInstance.create(NetWorkService::class.java)),
                FavouriteLocationsLocalDataSource(RoomDataBase.getInstance(requireContext()).getAllFavLoacations())
            )
        )
        alarmViewModel= ViewModelProvider(this, vmFactory).get(AlarmViewModel::class.java)
        binding.btnChooseLoacationOfTheAlarm.setOnClickListener {
            if (myAlarm == null || latitude ==null || longitude ==null ) {
                Toast.makeText(requireContext(), "There is no location to be saved", Toast.LENGTH_LONG).show()
            } else {
                DateDialog( latitude, longitude )
            }
        }
    }

    override fun onMapReady(p0: GoogleMap) {
        map = p0

        // Move camera and add the initial marker (Sydney)
        val sydney = LatLng(-34.0, 151.0)
        myMarker = map.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        map.moveCamera(CameraUpdateFactory.newLatLng(sydney))

        // Handle map click to add new markers and remove the previous one
        map.setOnMapClickListener { latLng ->
            // Remove the previous marker, whether it's the initial one or a new one
            myMarker?.remove()

            // Add a new marker at the clicked location
            myMarker = map.addMarker(MarkerOptions().position(latLng).title("New Marker"))

            // Move camera to the new location
            map.moveCamera(CameraUpdateFactory.newLatLng(latLng))

            // Get the address for the new marker
            val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            if (addresses != null && addresses.isNotEmpty()) {
                val address = addresses[0]
                locationAddress = address.getAddressLine(0) // the address of the new marker
                Log.d("TAG", "onMapReady: my address: $locationAddress ")
                latitude=latLng.latitude // lat
                longitude  =latLng.longitude // long

                Log.d("TAG", "onMapReady: ana fel alarm map lat&long $latitude , $longitude ")
                locationAddress // full addresss
                myAlarm= AlarmItem(AlarmDate="",address=locationAddress!!)
//                favLocation=
//                    FavouriteLocationItem(address=locationAddress!! , lat =  latLng.latitude, lng = latLng.longitude)



            } else {
                Log.d("TAG", "onMapReady: address is not found! ")
            }
        }
    }

    private fun DateDialog(lat :Double, lng : Double) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                val targtedDate = Calendar.getInstance().apply {
                    set(Calendar.YEAR, selectedYear)
                    set(Calendar.MONTH, selectedMonth)
                    set(Calendar.DAY_OF_MONTH, selectedDay)
                }
                showTimePicker(targtedDate , lat , lng)
            },
            year, month, day
        ).apply {
            datePicker.minDate = System.currentTimeMillis() - 1000
        }.show()
    }


    private fun showTimePicker(targtedDate: Calendar, lat : Double, lng : Double) {
        val hour = targtedDate.get(Calendar.HOUR_OF_DAY)
        val minute = targtedDate.get(Calendar.MINUTE)

        TimePickerDialog(
            requireContext(),
            { _, selectedHour, selectedMinute ->
                targtedDate.apply {
                    set(Calendar.HOUR_OF_DAY, selectedHour)
                    set(Calendar.MINUTE, selectedMinute)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                if (targtedDate.timeInMillis <= System.currentTimeMillis()) {
                    Toast.makeText(requireContext(), "you dont have a time machine to back in time ", Toast.LENGTH_LONG).show()
                } else {
                    setMyAlarm(targtedDate ,lat ,lng )

                }
            },
            hour, minute, false
        ).show()
    }

    @SuppressLint("ScheduleExactAlarm")
    private fun setMyAlarm(selectedDateTime: Calendar, latitude : Double, longitude:Double ) {
        val intent = Intent(requireContext(), AlarmBroadCastReciver::class.java)
        intent.putExtra("lat", latitude)
        intent.putExtra("long", longitude)

        pendingIntent = PendingIntent.getBroadcast(
            requireContext(), 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, selectedDateTime.timeInMillis,
            pendingIntent
        )


        myAlarm?.AlarmDate =  selectedDateTime.timeInMillis.toString()
        lifecycleScope.launch {
         val result  =   alarmViewModel.insertAalrm(myAlarm!!)
            if (result >0)
            {
                Toast.makeText(requireContext(), " Alarm added sucessfully ", Toast.LENGTH_LONG).show()

                // now mavigate to the alarmFragment
                val action = MapsAlarmFragmentDirections.actionMapsAlarmFragmentToNavAlarm()
                Navigation.findNavController(binding.root).navigate(action)
            }
            else
            {
                Toast.makeText(requireContext(), "Problem with saving Aalrm ", Toast.LENGTH_LONG).show()

            }
        }



    }

}