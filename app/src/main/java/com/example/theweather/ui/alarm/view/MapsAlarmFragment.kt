package com.example.theweather.ui.alarm.view

import android.location.Geocoder
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.theweather.R
import com.example.theweather.databinding.FragmentMapsAlarmBinding
import com.example.theweather.model.FavouriteLocationItem

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.util.Locale

class MapsAlarmFragment : Fragment() , OnMapReadyCallback {
lateinit var binding : FragmentMapsAlarmBinding
    lateinit var map: GoogleMap
    private var myMarker: Marker? = null
    private lateinit var geocoder: Geocoder
    private var locationAddress: String? = null
    var favLocation : FavouriteLocationItem? = null

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
                latLng.latitude // lat
                latLng.longitude // long
                locationAddress // full addresss
                favLocation=
                    FavouriteLocationItem(address=locationAddress!! , lat =  latLng.latitude, lng = latLng.longitude)

            } else {
                Log.d("TAG", "onMapReady: address is not found! ")
            }
        }
    }
}