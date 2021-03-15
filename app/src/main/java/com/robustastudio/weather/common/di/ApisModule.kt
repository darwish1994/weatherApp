package com.robustastudio.weather.common.di

import com.robustastudio.weather.common.network.api.WeatherApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import retrofit2.Retrofit

@Module
@InstallIn(ViewModelComponent::class)
class ApisModule {
    @Provides
    fun provideWeatherApiCalls(retrofit: Retrofit): WeatherApi {
        return retrofit.create(WeatherApi::class.java)
    }


}