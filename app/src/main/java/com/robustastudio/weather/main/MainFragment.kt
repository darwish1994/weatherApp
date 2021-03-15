package com.robustastudio.weather.main

import androidx.navigation.fragment.findNavController
import com.robustastudio.weather.R
import com.robustastudio.weather.common.base.BaseFragment
import com.robustastudio.weather.databinding.FragmentMainBinding


class MainFragment : BaseFragment<FragmentMainBinding>() {


    override fun initClick() {
        binding.addBtn.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_addNewTemperateFragment)
        }
    }

    override fun getViewBinding(): FragmentMainBinding? =
        FragmentMainBinding.inflate(layoutInflater)


}