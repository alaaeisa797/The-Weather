package com.example.theweather.ui.setting

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.location.Geocoder
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.example.theweather.MyConstants
import com.example.theweather.R
import com.example.theweather.databinding.FragmentMapsFavouriteBinding
import com.example.theweather.databinding.FragmentSettingMapBinding
import com.example.theweather.model.FavouriteLocationItem

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.util.Locale

class SettingMapFragment : Fragment() , OnMapReadyCallback {

    lateinit var binding  : FragmentSettingMapBinding
    private lateinit var geocoder: Geocoder
    lateinit var map: GoogleMap
    private var locationAddress: String? = null
    private var myMarker: Marker? = null

    lateinit var sharedPreferences: SharedPreferences
    lateinit var favLocation : FavouriteLocationItem

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingMapBinding.inflate(inflater,container,false)
        sharedPreferences = requireActivity().getSharedPreferences(MyConstants.MY_SHARED_PREFERANCE,
            Context.MODE_PRIVATE)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        geocoder = Geocoder(requireContext(), Locale.getDefault())
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        binding.btnLocateLocationSetting.setOnClickListener{
            if (favLocation == null) {
                Toast.makeText(requireContext(), "There is no location to be saved", Toast.LENGTH_LONG).show()
            } else {
                AlertDialog.Builder(context)
                    .setTitle("Confirom Location")
                    .setMessage("are your sure that your want to show this location forecast ?")
                    .setPositiveButton("Yes") { dialog, _ ->
                        val action = SettingMapFragmentDirections.actionSettingMapFragmentToNavHome()
                        Navigation.findNavController(binding.root).navigate(action)
                    }
                    .setNegativeButton("No") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()

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
                val lat =latLng.latitude // lat
                val lng = latLng.longitude // long
                locationAddress // full addresss
                sharedPreferences.edit().putString(MyConstants.MY_LOCATION_WAY,"MapSetting,$lat,$lng").apply()
                favLocation=FavouriteLocationItem(address=locationAddress!! , lat =  latLng.latitude, lng = latLng.longitude)

            } else {
                Log.d("TAG", "onMapReady: address is not found! ")
            }
        }

    }
}