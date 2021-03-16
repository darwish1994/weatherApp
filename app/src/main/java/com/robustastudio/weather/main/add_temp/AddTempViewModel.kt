package com.robustastudio.weather.main.add_temp

import com.robustastudio.weather.common.base.BaseViewModel
import com.robustastudio.weather.common.dp.TempHistory
import com.robustastudio.weather.common.dp.TempHistoryOp
import com.robustastudio.weather.common.network.IViewState
import com.robustastudio.weather.common.network.response.TemperatureResponse
import com.robustastudio.weather.common.repo.WeatherRepo
import com.robustastudio.weather.common.utils.lifecyclle.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import io.realm.Realm
import javax.inject.Inject

@HiltViewModel
class AddTempViewModel @Inject constructor(private val weatherRepo: WeatherRepo) : BaseViewModel() {

    private val weatherLiveData: SingleLiveEvent<IViewState<TemperatureResponse>> by lazy { SingleLiveEvent() }
    private val realm by lazy { Realm.getDefaultInstance() }



    fun getWeatherLIveData() = weatherLiveData

    fun getWeatherByLocation(lat: Double, long: Double) = weatherRepo.getWeatherByLocation(lat, long).execute(weatherLiveData)

    fun saveTempToHistory(file: String) {
        val data = TempHistory(filePath = file)
        weatherLiveData.value?.fetchData()?.let {
            data.location = it.name
            data.temp = it.main?.temp
            data.maxTemp = it.main?.tempMax
            data.minTemp = it.main?.tempMin
            data.wind = it.wind?.speed
            if (it.weather?.isNotEmpty() == true)
                data.weather = it.weather!![0].main

        }
        TempHistoryOp.addNewTempHistory(data)
    }

    fun getTempHistoryById(id: Long) = TempHistoryOp.getTempHistoryById(realm, id)
    override fun onCleared() {
        super.onCleared()
        realm.close()
    }

}