package com.example.theweather.ui.alarm.view

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.theweather.ApiState
import com.example.theweather.databinding.FragmentAlarmBinding
import com.example.theweather.db.FavouriteLocationsLocalDataSource
import com.example.theweather.db.RoomDataBase
import com.example.theweather.favourite.view.OnClickListner
import com.example.theweather.favourite.view_model.FavouriteViewModel
import com.example.theweather.favourite.view_model.FavouriteViewModelFactory
import com.example.theweather.model.AlarmItem
import com.example.theweather.model.Reposatory
import com.example.theweather.network.NetWorkService
import com.example.theweather.network.RemoteDataSource
import com.example.theweather.network.RetrofitHelper
import com.example.theweather.ui.alarm.view_model.AlarmViewModel
import com.example.theweather.ui.alarm.view_model.AlarmViewModelFactory
import kotlinx.coroutines.launch


class AlarmFragment : Fragment() , OnClickListner<AlarmItem> {

    lateinit var binding: FragmentAlarmBinding

lateinit var vmFactory:AlarmViewModelFactory
lateinit var alarmViewModel:AlarmViewModel
lateinit var alarmApadter : AlarmAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAlarmBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vmFactory = AlarmViewModelFactory(
            Reposatory.getInstance(
                RemoteDataSource.getInstance(RetrofitHelper.retrofitInstance.create(NetWorkService::class.java)),
                FavouriteLocationsLocalDataSource(RoomDataBase.getInstance(requireContext()).getAllFavLoacations())
            )
        )
        alarmViewModel= ViewModelProvider(this, vmFactory).get(AlarmViewModel::class.java)

        alarmViewModel.getAllAlarms()

        alarmApadter = AlarmAdapter(this)

        binding.recyclerView .apply {
            adapter = alarmApadter
            layoutManager = LinearLayoutManager(requireContext())
        }

        observOnAllAlarms()
       binding.floatingActionButton4.setOnClickListener{

        val action =
            AlarmFragmentDirections.actionNavAlarmToMapsAlarmFragment()
        Navigation.findNavController(binding.root).navigate(action)

    }
    }

    fun observOnAllAlarms ()
    {
        lifecycleScope.launch {
            alarmViewModel.localLiveData .collect { result ->
                when (result) {
                    is ApiState.Loading -> {

                        Log.d("TAG", "onCreateView: Loading case ")
                    }

                    is ApiState.Success -> {
                        val myResult = result.data
                       alarmApadter.submitList(myResult)

                    }

                    else -> {

                        Log.d("TAG", "onCreateView: Faliure case ")
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

    }

    override fun OnClick(t: AlarmItem) {

        AlertDialog.Builder(context)
            .setTitle("Confirm Alarm Delete")
            .setMessage("Are you sure that you want to delete this alarm")
            .setPositiveButton("Yes") { dialog, _ ->
                lifecycleScope.launch {
                    val result = alarmViewModel.deleteAlarm (t)
                    if (result > 0) {
                        Toast.makeText(requireContext(), "deleted successfully", Toast.LENGTH_LONG).show()
                        alarmViewModel.getAllAlarms()
                    } else {
                        Toast.makeText(requireContext(), "Problem with deleting", Toast.LENGTH_LONG).show()
                    }
                }
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}