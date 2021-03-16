package com.robustastudio.weather.common.dp

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

open class TempHistory(
    @PrimaryKey
    var id: Long? = Date().time,
    var weather: String?=null,
    var filePath: String?=null,
    var location: String?=null,
    var temp: Double?=null,
    var maxTemp: Double?=null,
    var minTemp: Double?=null,
    var wind: Double?=null
) : RealmObject()