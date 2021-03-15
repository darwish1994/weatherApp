package com.robustastudio.weather.common.base


import android.os.Bundle
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.robustastudio.weather.R
import com.robustastudio.weather.common.utils.dialog.DialogUtils


abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {

    protected lateinit var binding: VB

    private val loadingDialog: AlertDialog by lazy {
        DialogUtils.setupLoadingDialog(
            this, LayoutInflater.from(this).inflate(R.layout.loading_layout, null, false)
        )
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        onPreSetContentView()// for in case of animation
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        binding = getViewBinding()
        setContentView(binding.root)

        initOnCreate()

    }

    abstract fun getViewBinding(): VB



    fun dismissLoading() {
        if (loadingDialog.isShowing)
            loadingDialog.dismiss()

    }

    fun showLoading() {
        if (!loadingDialog.isShowing)
            loadingDialog.show()

    }




    override fun onDestroy() {
        loadingDialog.dismiss()

        super.onDestroy()
    }

    open fun onPreSetContentView() {}
    protected abstract fun initOnCreate()

}