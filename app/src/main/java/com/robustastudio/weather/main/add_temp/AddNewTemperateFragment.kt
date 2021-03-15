package com.robustastudio.weather.main.add_temp

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.viewModels
import com.robustastudio.weather.common.network.IViewState
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.robustastudio.weather.common.base.BaseFragmentMVVM
import com.robustastudio.weather.common.network.CommonStatus
import com.robustastudio.weather.common.network.response.TemperatureResponse
import com.robustastudio.weather.common.utils.extention.loadFrom
import com.robustastudio.weather.common.utils.extention.showSuccessMessage
import com.robustastudio.weather.databinding.FragmentAddNewTempraterBinding
import com.robustastudio.weather.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.io.File
import java.io.IOException

@AndroidEntryPoint
class AddNewTemperateFragment :
    BaseFragmentMVVM<FragmentAddNewTempraterBinding, AddTempViewModel>(), View.OnClickListener {

    private var filePath: String? = null

    private val photoContract = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        if (it)
            filePath?.let { uri ->
                binding.pickedView.loadFrom(uri)
                binding.takePhoto.visibility = View.GONE
            }

    }


    override fun getViewBinding(): FragmentAddNewTempraterBinding =
        FragmentAddNewTempraterBinding.inflate(layoutInflater)


    override fun initClick() {
        binding.takePhoto.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.takePhoto.id -> openCamera()
        }
    }

    private fun openCamera() {
        Dexter.withContext(requireActivity()).withPermissions(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if (report!!.areAllPermissionsGranted()) {
                        val photoFile: File? = try {
                            createFile(".jpg")
                        } catch (ex: IOException) {
                            Timber.e(ex)
                            null
                        }
                        photoFile?.also {
                            val photoURI: Uri = FileProvider.getUriForFile(
                                requireActivity(),
                                "${activity?.packageName}.fileprovider",
                                it
                            )
                            filePath = photoURI.toString()
                            photoContract.launch(photoURI)
                        }


                    } else {
                        val dir = Intent()
                        dir.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        dir.data = Uri.fromParts("package", activity?.packageName, null)
                        startActivity(dir)

                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()

                }
            }).onSameThread().check()

    }

    override fun initViewModel(): Lazy<AddTempViewModel> = viewModels()

    override fun onCreateInit() {
        getViewModel().getWeatherByLocation(lat = 29.9770477, long = 31.2513537)
        getViewModel().getWeatherLIveData().observe(viewLifecycleOwner,::onFetchDataObserve)
    }

    private fun loading(it:Boolean){
        if (it)
            (activity as MainActivity).showLoading()
        else
            (activity as MainActivity).dismissLoading()

    }
    private fun onFetchDataObserve(it: IViewState<TemperatureResponse>?){
        when(it?.whichState()){

            CommonStatus.LOADING-> loading(true)
            CommonStatus.ERROR-> {
                loading(false)
                Timber.e(it.fetchError())
            }
            CommonStatus.SUCCESS->
            {
                loading(false)
                it.fetchData()?.let {
                    it.name?.let { it1 -> showSuccessMessage(it1) }

                }
            }


        }

    }


}