package com.robustastudio.weather.common.network


import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject

class ResponseInterceptor @Inject constructor(private val networkHelper: NetworkHelper) :
    Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {

        if (!networkHelper.isNetworkConnected()) throw  NoInternetConnection()


        return chain.proceed(getNewRequest(chain.request()))
    }

    private fun getNewRequest(request: Request) =
        getRequestHeaders(request).url(getRequestQueries(request)).build()

    private fun getRequestHeaders(request: Request) = request
        .newBuilder()
        .addHeader("Content-Type", "application/json")
        .addHeader("accept", "application/json")

    private fun getRequestQueries(request: Request) = request.url
        .newBuilder()
        .build()

    inner class NoInternetConnection() : IOException() {

        override val message: String
            get() = " تحقق من إتصال الانترنت "
    }
}
