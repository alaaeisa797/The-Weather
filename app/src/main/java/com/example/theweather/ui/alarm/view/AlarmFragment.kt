package com.example.theweather.ui.alarm.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.theweather.databinding.FragmentAlarmBinding
import com.example.theweather.ui.alarm.view_model.AlarmViewModel


class AlarmFragment : Fragment() {

    lateinit var binding: FragmentAlarmBinding




    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val slideshowViewModel =
            ViewModelProvider(this).get(AlarmViewModel::class.java)

        binding = FragmentAlarmBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    binding.floatingActionButton4.setOnClickListener{

        val action =
            AlarmFragmentDirections.actionNavAlarmToMapsAlarmFragment()
        Navigation.findNavController(binding.root).navigate(action)

    }
    }

    override fun onDestroyView() {
        super.onDestroyView()

    }
}