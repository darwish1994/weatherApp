package com.robustastudio.weather.main.add_temp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.robustastudio.weather.R
import com.robustastudio.weather.common.base.BaseFragmentMVVM
import com.robustastudio.weather.common.dp.TempHistory
import com.robustastudio.weather.common.network.CommonStatus
import com.robustastudio.weather.common.network.IViewState
import com.robustastudio.weather.common.network.response.TemperatureResponse
import com.robustastudio.weather.common.utils.Constants
import com.robustastudio.weather.common.utils.dialog.DialogUtils
import com.robustastudio.weather.common.utils.extention.loadFrom
import com.robustastudio.weather.common.utils.extention.showErrorMessage
import com.robustastudio.weather.common.utils.location.LocationUtil
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
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val photoContract = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        if (it)
            filePath?.let { uri ->
                binding.pickedView.loadFrom(uri)
                binding.takePhoto.visibility = View.GONE
                if (binding.view.visibility == View.VISIBLE)
                    binding.saveBtn.visibility = View.VISIBLE

            }

    }
    private lateinit var handler: Handler


    override fun onCreateInit() {

        //check for argument to know
        // if user want to open temp history or add new one
        // if it null or 0 that mean he want ta add new one
        // if anther it will show this by getting it from data base
        arguments?.getLong(Constants.TEMP_ID)?.let {
            if (it != 0L) {
                setHasOptionsMenu(true)
                getViewModel().getTempHistoryById(it)?.let { temp ->
                    updateDataView(temp)
                }
                return
            }
        }

        // show take photo button
        binding.takePhoto.visibility = View.VISIBLE
        // init observer for api response
        getViewModel().getWeatherLIveData().observe(viewLifecycleOwner, ::onFetchDataObserve)
        // ini fused location to get last location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        //check for permission and add listener for location on complete
        checkLocationPermission()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.share -> drawBitMapOfView()
        }

        return false
    }

    // private fun to show loading dialog
    // it used for show loading dialog of activity
    private fun loading(it: Boolean) {
        if (it)
            (activity as MainActivity).showLoading()
        else
            (activity as MainActivity).dismissLoading()

    }

    private fun updateDataView(tempHistory: TempHistory) {
        showDataView()
        binding.location.text = tempHistory.location
        binding.status.text = tempHistory.weather
        binding.temp.text = getString(R.string.temp, tempHistory.temp.toString())
        binding.maxTemp.text = getString(R.string.temp_max, tempHistory.maxTemp.toString())
        binding.minTemp.text = getString(R.string.temp_min, tempHistory.minTemp.toString())
        binding.wind.text = getString(R.string.wind_speed, tempHistory.wind.toString())
        tempHistory.filePath?.let { binding.pickedView.loadFrom(it) }

    }


    //show all view when data load successfully from api or user want to view old temp history
    private fun showDataView() {
        binding.view.visibility = View.VISIBLE
        binding.location.visibility = View.VISIBLE
        binding.status.visibility = View.VISIBLE
        binding.maxTemp.visibility = View.VISIBLE
        binding.minTemp.visibility = View.VISIBLE
        binding.temp.visibility = View.VISIBLE
        binding.wind.visibility = View.VISIBLE
        if (filePath?.isNotBlank() == true)
            binding.saveBtn.visibility = View.VISIBLE
    }

    // open camera and save file to storage
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

    //get result of api
    private fun onFetchDataObserve(it: IViewState<TemperatureResponse>?) {
        when (it?.whichState()) {

            CommonStatus.LOADING -> loading(true)
            CommonStatus.ERROR -> {
                loading(false)
                //retry dialog if request fail
                DialogUtils.showGenericErrorPopup(
                    context = requireActivity(),
                    retryListener = {
                        checkLocationPermission()
                    },
                    isCancelable = false,
                    cancelListener = {

                    }
                )
                Timber.e(it.fetchError())
            }
            CommonStatus.SUCCESS -> {
                loading(false)
                it.fetchData()?.let {

                    showDataView()
                    //set location name
                    binding.location.text = it.name
                    //set temperature
                    it.main?.let { temp ->
                        binding.temp.text = getString(R.string.temp, temp.temp.toString())
                        binding.maxTemp.text = getString(R.string.temp_max, temp.tempMax.toString())
                        binding.minTemp.text = getString(R.string.temp_min, temp.tempMin.toString())
                    }
                    //set wind speed
                    it.wind?.let { wind ->
                        binding.wind.text = getString(R.string.wind_speed, wind.speed.toString())
                    }

                    if (it.weather?.isNotEmpty() == true)
                        binding.status.text = it.weather!![0].main

                }
            }


        }
    }

    // request location permission
    private fun checkLocationPermission() {
        Dexter.withContext(requireContext()).withPermissions(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
            .withListener(object : MultiplePermissionsListener {
                @SuppressLint("MissingPermission")
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if (report?.areAllPermissionsGranted()!!) {
                        if (!LocationUtil.isLocationEnabled(requireContext())) {
                            DialogUtils.showEnableLocation(requireContext())
                        } else {
                            fusedLocationClient.lastLocation.addOnSuccessListener(activity!!) { location ->
                                if (location != null) {
                                    getViewModel().getWeatherByLocation(
                                        location.latitude,
                                        location.longitude
                                    )
                                }
                            }
                        }
                    }

                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()

                }
            })
            .onSameThread().check()

    }

    // save current data in data base and go back
    private fun saveTemp() {
        if (filePath.isNullOrBlank()) {
            showErrorMessage("please take photo")
            return
        }
        if (getViewModel().getWeatherLIveData().value?.fetchData() == null) {
            showErrorMessage("please wait until fetch wether data")
            return
        }
        getViewModel().saveTempToHistory(filePath!!)

        findNavController().popBackStack()
    }

    override fun getViewBinding(): FragmentAddNewTempraterBinding =
        FragmentAddNewTempraterBinding.inflate(layoutInflater)

    override fun initViewModel(): Lazy<AddTempViewModel> = viewModels()

    override fun initClick() {
        binding.takePhoto.setOnClickListener(this)
        binding.saveBtn.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.takePhoto.id -> openCamera()
            binding.saveBtn.id -> saveTemp()
        }
    }

    // get bit map of view and draw it as bit map
    private fun drawBitMapOfView() {
        if (!::handler.isInitialized)
            handler = Handler(Looper.getMainLooper())
        handler.post(drawarRunable)
    }

    private val drawarRunable = Runnable {
        val b = Bitmap.createBitmap(
            binding.root.width,
            binding.root.height,
            Bitmap.Config.ARGB_8888
        )
        val c = Canvas(b)
        binding.root.draw(c)
        shareBitmap(b)
    }

    override fun onDestroyView() {
        if (::handler.isInitialized)
            handler.removeCallbacks(drawarRunable)

        super.onDestroyView()
    }

}