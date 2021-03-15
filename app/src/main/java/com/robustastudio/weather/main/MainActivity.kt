package com.robustastudio.weather.main

import com.robustastudio.weather.common.base.BaseActivity
import com.robustastudio.weather.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {

    override fun getViewBinding(): ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)

    override fun initOnCreate() {
    }

}