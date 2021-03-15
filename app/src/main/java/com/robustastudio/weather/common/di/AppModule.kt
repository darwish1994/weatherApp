package com.robustastudio.weather.common.di

import androidx.constraintlayout.widget.Constraints
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
class AppModule {


    @Provides
    fun providesGson() = Gson()

}