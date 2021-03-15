package com.robustastudio.weather.common.model


import com.google.gson.annotations.SerializedName

data class Sys(
    @SerializedName("country")
    var country: String? = null,
    @SerializedName("id")
    var id: Int? = null,
    @SerializedName("sunrise")
    var sunrise: Int? = null,
    @SerializedName("sunset")
    var sunset: Int? = null,
    @SerializedName("type")
    var type: Int? = null
)