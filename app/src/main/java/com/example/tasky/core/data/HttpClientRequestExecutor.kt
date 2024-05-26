package com.example.tasky.core.data

import android.util.Log
import com.example.tasky.BuildConfig
import com.example.tasky.auth.domain.HttpError
import com.example.tasky.auth.domain.Result
import com.example.tasky.auth.domain.RootError
import com.example.tasky.auth.domain.isSuccess
import com.example.tasky.core.data.dto.RefreshTokenRequest
import com.example.tasky.core.data.dto.RefreshTokenResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpMethod
import org.koin.java.KoinJavaComponent
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.concurrent.CancellationException

val prefs: Preferences by KoinJavaComponent.inject(Preferences::class.java)

suspend inline fun <reified P, R> HttpClient.executeRequest(
    httpMethod: HttpMethod,
    url: String,
    payload: P? = null,
    tag: String,
    noinline handleResponse: (response: HttpResponse) -> Result<R, RootError>
): Result<R, RootError> {
    val request = HttpRequestBuilder().apply {
        method = httpMethod
        url(url)
        payload?.let { setBody(it) }
    }

    return if (request.isAuthenticationRequest()) {
        executeRequest(request, tag, handleResponse)
    } else if (isAccessTokenValid()) {
        executeRequest(request, tag, handleResponse)
    } else {
        refreshToken(request, tag, handleResponse)
    }
}

suspend fun <R> HttpClient.refreshToken(
    requestBuilder: HttpRequestBuilder,
    tag: String,
    handleResponse: (response: HttpResponse) -> Result<R, RootError>
): Result<R, RootError> {
    return try {
        val accessTokenUrl = "${BuildConfig.BASE_URL}/accessToken"
        val refreshToken = prefs.getEncryptedString(Preferences.KEY_REFRESH_TOKEN, "")
        val userId = prefs.getEncryptedString(Preferences.KEY_USER_ID, "")

        val tokenRefreshResponse = request {
            method = HttpMethod.Post
            url(accessTokenUrl)
            setBody(RefreshTokenRequest(refreshToken, userId))
        }

        if (tokenRefreshResponse.isSuccess()) {
            val response = tokenRefreshResponse.body<RefreshTokenResponse>()

            prefs.putEncryptedString(Preferences.KEY_ACCESS_TOKEN, response.accessToken)
            prefs.putEncryptedLong(Preferences.KEY_ACCESS_TOKEN_EXPIRATION_TIME, response.expirationTimestamp)

            executeRequest(requestBuilder, tag, handleResponse)
        } else {
            Result.Error(
                when (tokenRefreshResponse.status.value) {
                    in 300..399 -> HttpError.REDIRECT
                    401 -> HttpError.UNAUTHORIZED
                    408 -> HttpError.REQUEST_TIMEOUT
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

suspend fun <R> HttpClient.executeRequest(
    request: HttpRequestBuilder,
    tag: String,
    handleResponse: (response: HttpResponse) -> Result<R, RootError>
): Result<R, RootError> {
    return try {
        val httpResponse = request(request)

        if (httpResponse.isSuccess()) {
            handleResponse.invoke(httpResponse)
        } else {
            if (request.isAuthenticationRequest() && httpResponse.isUnauthorised()) {
                refreshToken(request, tag, handleResponse)
            } else {
                Result.Error(
                    when (httpResponse.status.value) {
                        in 300..399 -> HttpError.REDIRECT
                        401 -> HttpError.UNAUTHORIZED
                        408 -> HttpError.REQUEST_TIMEOUT
                        409 -> getDefault409HttpError(request.url.toString())
                        413 -> HttpError.PAYLOAD_TOO_LARGE
                        in 400..499 -> HttpError.CLIENT_REQUEST
                        in 500..599 -> HttpError.SERVER_RESPONSE
                        else -> HttpError.UNKNOWN
                    }
                )
            }
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

fun isAccessTokenValid(): Boolean {
    val expirationTimeStamp = prefs.getEncryptedLong(Preferences.KEY_ACCESS_TOKEN_EXPIRATION_TIME, 0)
    val expirationDate =
        ZonedDateTime.ofInstant(Instant.ofEpochMilli(expirationTimeStamp), ZoneId.systemDefault()).toLocalDateTime()
    return expirationDate.isAfter(LocalDateTime.now())
}

fun getDefault409HttpError(url: String): HttpError {
    return when {
        url.contains("login") -> HttpError.CONFLICT_LOGIN
        url.contains("register") -> HttpError.CONFLICT_SIGN_UP
        else -> HttpError.UNKNOWN
    }
}

fun HttpResponse.isUnauthorised() = status.value == 401

fun HttpRequestBuilder.isAuthenticationRequest(): Boolean {
    val url = url.toString()
    return url.contains("login") || url.contains("register") || url.contains("logout") || url.contains("authenticate")
}
