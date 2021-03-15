package com.robustastudio.weather.main.add_temp

import com.robustastudio.weather.common.network.IViewState
import com.robustastudio.weather.common.base.BaseViewModel
import com.robustastudio.weather.common.network.response.TemperatureResponse
import com.robustastudio.weather.common.repo.WeatherRepo
import com.robustastudio.weather.common.utils.lifecyclle.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddTempViewModel @Inject constructor(private val weatherRepo: WeatherRepo) : BaseViewModel() {

    private val weatherLiveData: SingleLiveEvent<IViewState<TemperatureResponse>> by lazy { SingleLiveEvent() }

    fun getWeatherLIveData() = weatherLiveData

    fun getWeatherByLocation(lat: Double, long: Double) =
        weatherRepo.getWeatherByLocation(lat, long).execute(weatherLiveData)


}