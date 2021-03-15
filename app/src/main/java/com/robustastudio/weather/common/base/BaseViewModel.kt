package com.robustastudio.weather.common.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arg.awfar.chat.agent.network.IViewState
import com.arg.awfar.chat.agent.network.Result
import com.robustastudio.weather.common.network.RetrofitException
import com.robustastudio.weather.R
import com.robustastudio.weather.common.network.ResponseInterceptor
import com.robustastudio.weather.common.utils.extention.addTo
import com.robustastudio.weather.common.utils.extention.applySchedulers
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject


@HiltViewModel
open class BaseViewModel @Inject constructor() : ViewModel() {

    private val compositeDisposable: CompositeDisposable by lazy { CompositeDisposable() }
    fun <T> Single<T>.execute(state: MutableLiveData<IViewState<T>>) {
        state.value = Result.loading()
        this.compose(applySchedulers<T>())
            .subscribe({
                Timber.v("success call ")
                state.value = Result.success(it)
            }, { throwable ->
                Timber.v("exception isIO${throwable is IOException} ${throwable.cause} ${throwable.message} ${throwable.localizedMessage}")
                state.value = when (throwable) {


                    is IOException -> Result.error(
                        RetrofitException.unexpectedError(
                            throwable, messageId = R.string.io_exception
                        )
                    )
                    is SocketTimeoutException -> Result.error(RetrofitException.TimeOutError())

                    is ResponseInterceptor.NoInternetConnection ->
                        Result.error(RetrofitException.networkError(IOException(throwable)))
                    is com.jakewharton.retrofit2.adapter.rxjava2.HttpException ->
                        Result.error(RetrofitException.httpError(throwable.response()))
                    is HttpException ->
                        Result.error(RetrofitException.httpError(throwable.response()))

                    /**
                     * handle json parsing exceptions
                     * */
                    //is  JsonParseException ,is JsonSyntaxException, is JSONException, is JsonIOException
                    /**
                     * handle realm db exceptions
                     * */
                    //is IllegalStateException
                    else ->
                        Result.error(RetrofitException.unexpectedError(throwable))
                }
            }
            ).addTo(`access$compositeDisposable`)

    }


    @PublishedApi
    internal val `access$compositeDisposable`: CompositeDisposable
        get() = compositeDisposable


    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
        compositeDisposable.clear()
    }

}