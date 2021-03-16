package com.robustastudio.weather.common.base

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.annotation.NonNull
import androidx.core.content.FileProvider
import androidx.viewbinding.ViewBinding
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

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

    fun shareBitmap(@NonNull bitmap: Bitmap) {
        //---Save bitmap to external cache directory---//
        //get cache directory
        val cachePath = File(activity?.externalCacheDir, "my_images/")
        cachePath.mkdirs()

        //create png file
        val file = File(cachePath, "${Date().time}.png")
        val fileOutputStream: FileOutputStream
        try {
            fileOutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
            fileOutputStream.flush()
            fileOutputStream.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }


        //---Share File---//
        //get file uri
        val myImageFileUri: Uri = FileProvider.getUriForFile(
            requireActivity(),
            "${activity?.packageName}.fileprovider",
            file
        )

        //create a intent
        val intent = Intent(Intent.ACTION_SEND)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.putExtra(Intent.EXTRA_STREAM, myImageFileUri)
        intent.type = "image/png"

        startActivity(Intent.createChooser(intent, "Share with"))
    }

}