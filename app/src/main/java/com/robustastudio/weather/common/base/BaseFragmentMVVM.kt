package com.robustastudio.weather.common.base

import android.os.Bundle
import android.view.View
import androidx.viewbinding.ViewBinding

abstract class BaseFragmentMVVM<VB : ViewBinding, VM : BaseViewModel>() : BaseFragment<VB>() {
    private lateinit var mViewModel: Lazy<VM>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = initViewModel()


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
         onCreateInit()

    }


    abstract fun initViewModel(): Lazy<VM>

    abstract fun onCreateInit()
    fun getViewModel() = mViewModel.value
}