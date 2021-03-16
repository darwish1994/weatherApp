package com.robustastudio.weather.common.listener

import android.graphics.Bitmap

interface TempHistoryListener {

    fun <T> onItemClick(item: T)
    fun shareView(image: Bitmap)

}