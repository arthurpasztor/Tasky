package com.example.tasky.core.data

import android.util.Log
import com.example.tasky.auth.domain.isSuccess
import com.example.tasky.core.domain.DataError
import com.example.tasky.core.domain.DataError.HttpError
import com.example.tasky.core.domain.Result
import io.ktor.client.HttpClient
import io.ktor.client.request.parameter
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpMethod
import java.util.concurrent.CancellationException

suspend inline fun <reified P, R> HttpClient.executeRequest(
    httpMethod: HttpMethod,
    url: String,
    queryParams: Pair<String, Any>? = null,
    payload: P? = null,
    tag: String,
    handleResponse: (response: HttpResponse) -> Result<R, DataError>
): Result<R, DataError> {
    return try {
        val httpResponse = request {
            method = httpMethod
            url(url)
            queryParams?.let { parameter(it.first, it.second) }
            payload?.let { setBody(it) }
        }

        if (httpResponse.isSuccess()) {
            handleResponse.invoke(httpResponse)
        } else {
            Result.Error(
                when (httpResponse.status.value) {
                    in 300..399 -> HttpError.REDIRECT
                    401 -> HttpError.UNAUTHORIZED
                    408 -> HttpError.REQUEST_TIMEOUT
                    409 -> getDefault409HttpError(url)
                    413 -> HttpError.PAYLOAD_TOO_LARGE
                    in 400..499 -> HttpError.CLIENT_REQUEST
                    in 500..599 -> HttpError.SERVER_RESPONSE
                    else -> HttpError.UNKNOWN
                }
            )
        }
    } catch (e: Exception) {
        if (e is CancellationException) {
            throw e
        } else {
            Log.e(tag, "Error: ${e.message} / ${e.cause}")
            Result.Error(HttpError.UNKNOWN)
        }
    }
}

fun getDefault409HttpError(url: String): HttpError {
    return when {
        url.contains("login") -> HttpError.CONFLICT_LOGIN
        url.contains("register") -> HttpError.CONFLICT_SIGN_UP
        else -> HttpError.UNKNOWN
    }
}