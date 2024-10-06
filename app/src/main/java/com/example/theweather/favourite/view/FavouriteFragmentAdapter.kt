package com.example.theweather.favourite.view

import android.content.Context
import android.location.Geocoder
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.theweather.databinding.CurrentDayHourItemBinding
import com.example.theweather.databinding.FavoriteLocationItemBinding
import com.example.theweather.model.FavouriteLocationItem

class FavouriteFragmentAdapter ( var  onClick : OnClickListner<FavouriteLocationItem>) : ListAdapter<FavouriteLocationItem , FavouriteFragmentAdapter.FavouriteFragmentViewHolder>(FavouriteDiffUtil())
{
    lateinit var binding : FavoriteLocationItemBinding



    class FavouriteFragmentViewHolder ( val binding : FavoriteLocationItemBinding) : RecyclerView.ViewHolder(binding.root){}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteFragmentViewHolder {
        val layoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = FavoriteLocationItemBinding.inflate(layoutInflater, parent , false)
        return FavouriteFragmentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavouriteFragmentViewHolder, position: Int) {
        val currentFavLocation = getItem(position)
        holder.binding.tvFavLocation.text = getReadableLocation(currentFavLocation.lat,currentFavLocation.lng,holder.itemView.context)

        holder.binding.ivDeleteFromFav.setOnClickListener{

            onClick.OnClick(currentFavLocation)
        }
        holder.binding.cvFavLocation.setOnClickListener{
            Log.d("TAG", "onBindViewHolder: in FavLocationcard click latitude ${currentFavLocation.lat} ")
            Log.d("TAG", "onBindViewHolder: in FavLocationcard click longtude ${currentFavLocation.lng} ")

            //val action = FavouriteFragmentDirections.actionNavFavouriteToNavHome()
            val action = FavouriteFragmentDirections.actionNavFavouriteToNavHome().apply {
                myFullLocationInfo="FavLocation,${currentFavLocation.lat},${currentFavLocation.lng}"
            }
            Navigation.findNavController(binding.root).navigate(action)

             }
    }

    fun getReadableLocation(latitude: Double, longitude: Double,context: Context): String {
        val geocoder = Geocoder(context)
        val readableLocation = geocoder.getFromLocation(latitude, longitude, 1)
        val address = readableLocation?.get(0)

        val city = address?.subLocality ?: address?.locality ?: "Unknown City"
        val state = address?.adminArea ?: "Unknown State"
        val country = address?.countryName ?: "Unknown Country"

        return "$city, $state ,$country"
    }

}