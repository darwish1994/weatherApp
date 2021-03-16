package com.robustastudio.weather.main.temp_history

import com.robustastudio.weather.common.base.BaseViewModel
import com.robustastudio.weather.common.dp.TempHistoryOp
import io.realm.Realm


class HistoryViewModel : BaseViewModel() {

    private val realm by lazy { Realm.getDefaultInstance() }

    fun getTempHistoryAsync()=TempHistoryOp.getallTempHistory(realm)

    override fun onCleared() {
        super.onCleared()
        realm.close()
    }

}