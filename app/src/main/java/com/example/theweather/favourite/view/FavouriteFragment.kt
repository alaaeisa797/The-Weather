package com.example.theweather.favourite.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.theweather.ApiState
import com.example.theweather.R
import com.example.theweather.databinding.FragmentFavouriteBinding
import com.example.theweather.db.FavouriteLocationsLocalDataSource
import com.example.theweather.db.RoomDataBase
import com.example.theweather.favourite.view_model.FavouriteViewModel
import com.example.theweather.favourite.view_model.FavouriteViewModelFactory
import com.example.theweather.model.FavouriteLocationItem
import com.example.theweather.model.Reposatory
import com.example.theweather.network.NetWorkService
import com.example.theweather.network.RemoteDataSource
import com.example.theweather.network.RetrofitHelper
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class FavouriteFragment : Fragment() , OnClickListner<FavouriteLocationItem> {
lateinit var binding :FragmentFavouriteBinding
    lateinit var favouriteViewModel: FavouriteViewModel
    lateinit var vmFactory : FavouriteViewModelFactory

    lateinit var favouriteFragmentAdapter: FavouriteFragmentAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentFavouriteBinding.inflate(inflater,container,false)
        return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vmFactory = FavouriteViewModelFactory(
            Reposatory.getInstance(
                RemoteDataSource.getInstance(RetrofitHelper.retrofitInstance.create(NetWorkService::class.java)),
                FavouriteLocationsLocalDataSource(RoomDataBase.getInstance(requireContext()).getAllFavLoacations())
            )
        )
        favouriteViewModel= ViewModelProvider(this, vmFactory).get(FavouriteViewModel::class.java)
        favouriteViewModel.getFavLoacations()
        favouriteFragmentAdapter = FavouriteFragmentAdapter(this)
        binding.rvFavoriteLocationItem.apply {
            adapter = favouriteFragmentAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
         binding.floatingActionButton.setOnClickListener{

         val action = FavouriteFragmentDirections.actionNavFavouriteToMapsFavouriteFragment()
                     Navigation.findNavController(view).navigate(action)
        }

        lifecycleScope.launch {
            favouriteViewModel.localLiveData.collect{ result ->
                when (result)
                {
                    is ApiState.Failure -> {

                    }
                    is ApiState.Loading -> {}
                    is ApiState.Success -> {

                        favouriteFragmentAdapter.submitList(result.data)
                    }
                }

            }


        }

    }

    override fun OnClick(t: FavouriteLocationItem) { // delete click
   lifecycleScope.launch {
            val result = favouriteViewModel.delete(t)
            if (result > 0) {
                Toast.makeText(requireContext(), "deleted successfully", Toast.LENGTH_LONG).show()
                favouriteViewModel.getFavLoacations()
            } else {
                Toast.makeText(requireContext(), "Problem with deleting", Toast.LENGTH_LONG).show()
            }
        }
    }

}