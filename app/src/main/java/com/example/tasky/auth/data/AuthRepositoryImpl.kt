package com.example.tasky.auth.data

import android.util.Log
import com.example.tasky.BuildConfig
import com.example.tasky.auth.data.dto.LoginRequest
import com.example.tasky.auth.data.dto.SignUpRequest
import com.example.tasky.auth.data.dto.TokenResponse
import com.example.tasky.auth.domain.HttpError
import com.example.tasky.auth.domain.Result
import com.example.tasky.auth.domain.RootError
import com.example.tasky.auth.domain.isSuccess
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpMethod
import java.util.concurrent.CancellationException

class AuthRepositoryImpl(private val client: HttpClient) : AuthRepository {

    override suspend fun login(info: LoginRequest): Result<Unit, RootError> {
        return executeRequest<LoginRequest, Unit>(HttpMethod.Post, loginUrl, info) {
            val response = it.body<TokenResponse>()

            // TODO save token to preferences

            Result.Success(Unit)
        }
    }

    override suspend fun signUp(info: SignUpRequest): Result<LoginRequest, RootError> {
        return executeRequest<SignUpRequest, LoginRequest>(HttpMethod.Post, signUpUrl, info) {
            Result.Success(LoginRequest(info.email, info.password))
        }
    }

    private suspend inline fun <reified P, R> executeRequest(
        httpMethod: HttpMethod,
        url: String,
        payload: P? = null,
        handleResponse: (response: HttpResponse) -> Result<R, RootError>
    ): Result<R, RootError> {
        return try {
            val httpResponse = client.request {
                method = httpMethod
                url(url)
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
                        409 -> {
                            when (url) {
                                loginUrl -> HttpError.CONFLICT_LOGIN
                                signUpUrl -> HttpError.CONFLICT_SIGN_UP
                                else -> HttpError.UNKNOWN
                            }
                        }

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
                Log.e(TAG, "Error: ${e.message} / ${e.cause}")
                Result.Error(HttpError.UNKNOWN)
            }
        }
    }

    private val loginUrl = "${BuildConfig.BASE_URL}/login"
    private val signUpUrl = "${BuildConfig.BASE_URL}/register"

    companion object {
        private const val TAG = "AuthRepository"
    }
}