package com.robustastudio.weather.common.network

import androidx.annotation.StringRes
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.annotations.SerializedName
import com.robustastudio.weather.R
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException

class RetrofitException(
    override val message: String?,
    @StringRes
    val messageResourceId: Int?,
    private val responseBody: String?,
    val responseCode: Int,
    /**
     * The event kind which triggered this error.
     */
    val kind: Kind,
    val exception: Throwable?,
    val errorCode: Int
) : RuntimeException(exception) {


    @Throws(IOException::class, JsonSyntaxException::class)
    fun <T> getErrorBodyAs(type: Class<T>?): T? {
        return if (responseBody == null || responseBody.isEmpty()) {
            null
        } else sGson.fromJson(responseBody, type)
    }


    enum class Kind {

        NETWORK,

        /**
         * A non-200 HTTP status code was received from the server.
         */
        HTTP,

        /**
         * An internal error occurred while attempting to execute a request. It is best practice to
         * re-throw this exception so your application crashes.
         */
        UNEXPECTED
    }

    companion object {

        private val sGson = Gson()

        @JvmStatic
        fun httpError(response: Response<*>?): RetrofitException {
            var message: String? = null
            var responseBody: String? = ""
            var responseCode = 0
            var errorCode = 0
            var messageResourceId: Int? = null
            if (response != null) {
                responseCode = response.code()
                messageResourceId = getErrorCodeMessage(responseCode)
            }

            try {

                if (response?.errorBody() != null) {
                    responseBody = response.errorBody()!!.string()
                }

                val apiErrorJson: BaseResponse<*> = sGson.fromJson(
                    responseBody,
                    BaseResponse::class.java
                )

                if (apiErrorJson.result != null) {
                    message = apiErrorJson.result!!.message
                    errorCode = apiErrorJson.result!!.code
                }
            } catch (e: Exception) {
                message = responseCode.toString() + " " + response!!.message()
                e.printStackTrace()
            }
            return RetrofitException(
                message ?: "Something went wrong",
                messageResourceId,
                responseBody,
                responseCode,
                Kind.HTTP,
                null,
                errorCode
            )
        }

        fun TimeOutError() = RetrofitException(
            "timeout",
            R.string.network_time_out,
            null,
            0,
            Kind.NETWORK,
            SocketTimeoutException(),
            0
        )


    @JvmStatic
    fun networkError(exception: IOException): RetrofitException {
        return RetrofitException(
            exception.message ?: "",
            R.string.no_internet_connection_error,
            null,
            0,
            Kind.NETWORK,
            exception,
            0
        )
    }

    @JvmStatic
    fun unexpectedError(
        exception: Throwable,
        messageId: Int = R.string.someting_wrong
    ): RetrofitException {
        return RetrofitException(
            exception.message,
            messageId,
            null,
            0,
            Kind.UNEXPECTED,
            exception,
            0
        )
    }


    private fun getErrorCodeMessage(code: Int): Int {
        return when (code) {
//            in (100..200) -> "informational  Error"
//            in (200..299) -> "success status "
//            in (300..399) -> "redirection status"
            405, in (500..599) -> R.string.server_error //HTTP 405 Method Not Allowed
            in (400..499) -> R.string.unAuthentication
            else -> R.string.someting_wrong

        }
    }
}
}

open class BaseResponse<T> {
    @SerializedName("result")
    var result: BaseResponseResult? = null

    @SerializedName("data")
    var data: T? = null
        private set

    fun setData(data: T) {
        this.data = data
    }

    inner class BaseResponseResult {
        @SerializedName("success")
        var isSuccess = false

        @SerializedName("code")
        var code = 0

        @SerializedName("message")
        var message: String? = null

    }
}