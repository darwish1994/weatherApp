package com.robustastudio.weather.common.repo

import com.robustastudio.weather.common.network.api.WeatherApi
import javax.inject.Inject

class WeatherRepo @Inject constructor(private val weatherApi: WeatherApi) {

    fun getWeatherByLocation(lat: Double, lon: Double) = weatherApi.getWeatherByLocation(lat, lon)

}