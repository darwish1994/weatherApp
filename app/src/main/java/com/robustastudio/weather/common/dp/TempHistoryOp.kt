package com.robustastudio.weather.common.dp

import io.realm.Realm
import io.realm.RealmResults

object TempHistoryOp {

    fun addNewTempHistory(data: TempHistory) {
        Realm.getDefaultInstance().use {
            it.executeTransactionAsync { realm_es ->
                realm_es.insertOrUpdate(data)
            }

        }
    }


    fun getTempHistoryById(realm: Realm, id: Long): TempHistory? {
        return realm.where(TempHistory::class.java).equalTo("id", id).findFirst()
    }

    fun getLatestTempHistory(realm: Realm): TempHistory? {
        return realm.where(TempHistory::class.java).sort("id").findFirst()
    }

    fun getallTempHistory(realm: Realm): RealmResults<TempHistory>? = realm.where(TempHistory::class.java).sort("id").findAllAsync()

}