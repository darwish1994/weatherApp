package com.robustastudio.weather.common.di

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.robustastudio.weather.BuildConfig
import com.robustastudio.weather.common.network.NetworkHelper
import com.robustastudio.weather.common.network.ResponseInterceptor
import com.robustastudio.weather.common.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {
    @Provides
    @Singleton
    fun provideRetrofitInstance(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    }


    @Provides
    @Singleton
    fun provideOKHTTPInstance(networkHelper: NetworkHelper): OkHttpClient {
        val httpClient = OkHttpClient().newBuilder()
            .connectTimeout(Constants.REQUEST_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .readTimeout(Constants.REQUEST_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .writeTimeout(Constants.REQUEST_TIMEOUT.toLong(), TimeUnit.SECONDS)

        httpClient.addInterceptor(ResponseInterceptor(networkHelper))
//            .authenticator(AuthenticatorInterceptor())
        if (BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            httpClient.addInterceptor(loggingInterceptor).build()
        }

        return httpClient.build()
    }


}