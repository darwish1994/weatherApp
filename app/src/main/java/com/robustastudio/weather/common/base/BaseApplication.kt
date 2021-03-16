package com.robustastudio.weather.common.base

import android.app.Application
import com.robustastudio.weather.BuildConfig
import dagger.hilt.android.HiltAndroidApp
import io.realm.Realm
import io.realm.RealmConfiguration
import timber.log.Timber
import javax.inject.Inject


@HiltAndroidApp
class BaseApplication : Application() {


    @Inject
    lateinit var realmConfiguration: RealmConfiguration


    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        realmConfig()


    }

    private fun realmConfig() = Realm.setDefaultConfiguration(realmConfiguration)


}