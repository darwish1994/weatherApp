package com.robustastudio.weather.common.network.api

import com.robustastudio.weather.common.network.response.TemperatureResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("weather")
    fun getWeatherByLocation(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double
    ): Single<TemperatureResponse>
}