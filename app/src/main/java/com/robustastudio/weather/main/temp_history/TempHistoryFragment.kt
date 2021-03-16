package com.robustastudio.weather.main.temp_history

import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.robustastudio.weather.R
import com.robustastudio.weather.common.base.BaseFragmentMVVM
import com.robustastudio.weather.common.dp.TempHistory
import com.robustastudio.weather.common.listener.TempHistoryListener
import com.robustastudio.weather.common.utils.Constants
import com.robustastudio.weather.databinding.FragmentMainBinding
import com.robustastudio.weather.main.adapter.TemperatureHistoryAdapter


class TempHistoryFragment : BaseFragmentMVVM<FragmentMainBinding, HistoryViewModel>() ,
    TempHistoryListener {

    private lateinit var adapter: TemperatureHistoryAdapter

    override fun initClick() {
        binding.addBtn.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_addNewTemperateFragment)
        }
    }


    override fun getViewBinding(): FragmentMainBinding = FragmentMainBinding.inflate(layoutInflater)

    override fun initViewModel(): Lazy<HistoryViewModel> = viewModels()
    override fun onCreateInit() {
        // init adapter with sync data which get in background thread
        adapter = TemperatureHistoryAdapter(this,getViewModel().getTempHistoryAsync())
        binding.tempRec.adapter = adapter
    }


    override fun <T> onItemClick(item: T) {
        if(item is TempHistory){
            findNavController().navigate(R.id.action_mainFragment_to_addNewTemperateFragment,
                Bundle().apply {
                    putLong(Constants.TEMP_ID,item.id?:0)
                }
            )

        }
    }

    override fun shareView(image: Bitmap) {
        shareBitmap(image)
    }




}