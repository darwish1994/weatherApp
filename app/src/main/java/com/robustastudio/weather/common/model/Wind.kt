package com.robustastudio.weather.common.model


import com.google.gson.annotations.SerializedName

data class Wind(
    @SerializedName("deg")
    var deg: Int? = null,
    @SerializedName("speed")
    var speed: Double? = null
)