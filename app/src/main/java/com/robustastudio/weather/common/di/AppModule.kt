package com.robustastudio.weather.common.di

import android.app.Application
import androidx.constraintlayout.widget.Constraints
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.realm.Realm
import io.realm.RealmConfiguration


@Module
@InstallIn(SingletonComponent::class)
class AppModule {


    @Provides
    fun providesGson() = Gson()

    @Provides
    fun provideRealmConfig(application: Application): RealmConfiguration {
        Realm.init(application)
        return RealmConfiguration.Builder()
            .name("weather_database")
            .schemaVersion(1)
            .deleteRealmIfMigrationNeeded()
            .build()
    }

}