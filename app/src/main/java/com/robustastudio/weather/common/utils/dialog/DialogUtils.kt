package com.robustastudio.weather.common.utils.dialog

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import com.github.ybq.android.spinkit.style.CubeGrid
import com.robustastudio.weather.R
import com.robustastudio.weather.databinding.PopupErrorBinding


object DialogUtils {

    fun showGenericErrorPopup(
        context: Context,
        retryListener: () -> Unit,
        cancelListener: () -> Unit?,
        isCancelable: Boolean,
        message: String? = null,
        view: PopupErrorBinding = PopupErrorBinding.inflate(
            LayoutInflater.from(context),
            null,
            false
        )


    ): AlertDialog {

        val alertDialog = AlertDialog.Builder(context).apply {
            setView(view.root)
            setCancelable(false)
        }.create()
        alertDialog.apply {
            message?.let {
                view.tvError.text = it
            }
            view.btnErrorRetry.setOnClickListener {
                retryListener.invoke()
                dismiss()
            }

            if (isCancelable) {
                view.apply {
                    btnErrorCancel.setOnClickListener {
                        dismiss()
                        cancelListener.invoke()
                    }
                    btnErrorCancel.visibility = View.VISIBLE
                }
            } else {
                view.btnErrorCancel.visibility = View.GONE
            }
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        }.show()

        return alertDialog

    }

    fun setupLoadingDialog(context: Context, view: View): AlertDialog =
        AlertDialog.Builder(context).apply {
            (view.findViewById<View>(R.id.spin_kit) as ProgressBar).indeterminateDrawable =
                CubeGrid()
            setView(view)
            setCancelable(false)
        }.create()
            .apply {
                window?.let {
                    it.attributes.windowAnimations = R.style.fadeInOutAnimation
                    it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                }
            }


    fun showEnableLocation(context: Context): AlertDialog {
        val alertDialog = AlertDialog.Builder(context).apply {
            setTitle(R.string.please_enable_location)
            setMessage(R.string.enable_GPS_from_setting)
            setCancelable(false)
        }.create()
        alertDialog.apply {
            setButton(DialogInterface.BUTTON_POSITIVE, "ok") { c, cc ->
                context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                c.dismiss()
            }

        }.show()

        return alertDialog
    }


}

//hp850