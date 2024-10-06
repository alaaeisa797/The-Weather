package com.example.theweather.favourite.view

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
import com.example.theweather.databinding.FragmentMapsFavouriteBinding
import com.example.theweather.db.FavouriteLocationsLocalDataSource
import com.example.theweather.db.RoomDataBase
import com.example.theweather.favourite.view_model.FavouriteViewModel
import com.example.theweather.favourite.view_model.FavouriteViewModelFactory
import com.example.theweather.model.FavouriteLocationItem
import com.example.theweather.model.Reposatory
import com.example.theweather.network.NetWorkService
import com.example.theweather.network.RemoteDataSource
import com.example.theweather.network.RetrofitHelper

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.launch
import java.util.Locale

class MapsFavouriteFragment : Fragment() , OnMapReadyCallback {

    private lateinit var geocoder: Geocoder
    private var locationAddress: String? = null
    private var myMarker: Marker? = null
    lateinit var map: GoogleMap
    lateinit var binding  : FragmentMapsFavouriteBinding
    lateinit var favLocation : FavouriteLocationItem
    lateinit var favouriteViewModel: FavouriteViewModel
    lateinit var vmFactory : FavouriteViewModelFactory
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapsFavouriteBinding.inflate(inflater,container,false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        geocoder = Geocoder(requireContext(), Locale.getDefault())
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        vmFactory = FavouriteViewModelFactory(
            Reposatory.getInstance(
                RemoteDataSource.getInstance(RetrofitHelper.retrofitInstance.create(NetWorkService::class.java)),
                FavouriteLocationsLocalDataSource(RoomDataBase.getInstance(requireContext()).getAllFavLoacations())
            )
        )

        favouriteViewModel= ViewModelProvider(this, vmFactory).get(FavouriteViewModel::class.java)

        binding.btnAddToFavourite.setOnClickListener {
            if (favLocation == null) {
                Toast.makeText(requireContext(), "There is no location to be saved", Toast.LENGTH_LONG).show()
            } else {

                lifecycleScope.launch {
                    val result = favouriteViewModel.insert(favLocation)
                    if (result > 0) {
                        // hean 7ot el direction to favScreenfragment
                        val action = MapsFavouriteFragmentDirections.actionMapsFavouriteFragmentToNavFavourite()
                        Navigation.findNavController(binding.root).navigate(action)
                        Toast.makeText(requireContext(), "Saved successfully", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(requireContext(), "Problem with saving", Toast.LENGTH_LONG).show()
                    }
                }
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
                latLng.latitude // lat
                latLng.longitude // long
                locationAddress // full addresss
                favLocation=FavouriteLocationItem(address=locationAddress!! , lat =  latLng.latitude, lng = latLng.longitude)

            } else {
                Log.d("TAG", "onMapReady: address is not found! ")
            }
        }


    }
}