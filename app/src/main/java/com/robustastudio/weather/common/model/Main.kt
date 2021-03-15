package com.robustastudio.weather.common.model


import com.google.gson.annotations.SerializedName

data class Main(
    @SerializedName("feels_like")
    var feelsLike: Double? = null,
    @SerializedName("humidity")
    var humidity: Int? = null,
    @SerializedName("pressure")
    var pressure: Int? = null,
    @SerializedName("temp")
    var temp: Double? = null,
    @SerializedName("temp_max")
    var tempMax: Double? = null,
    @SerializedName("temp_min")
    var tempMin: Double? = null
)