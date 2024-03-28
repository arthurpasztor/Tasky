package com.example.tasky.auth.data

import android.util.Log
import com.example.tasky.BuildConfig
import com.example.tasky.auth.data.dto.AuthError
import com.example.tasky.auth.data.dto.LoginRequest
import com.example.tasky.auth.data.dto.SignUpRequest
import com.example.tasky.auth.data.dto.TokenResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import java.util.concurrent.CancellationException

class AuthRepositoryImpl(private val client: HttpClient) : AuthRepository {

    override suspend fun login(info: LoginRequest): AuthResult {
        return try {
            val httpResponse = client.post {
                url(loginUrl)
                setBody(info)
            }

            if (httpResponse.status.value in 200..299) {
                val response = httpResponse.body<TokenResponse>()

                // TODO save token to preferences

                AuthResult.Authorized(info)
            } else {
                handleErrors(httpResponse)
            }
        } catch (e: Exception) {
            if (e is CancellationException) {
                throw e
            } else {
                Log.e(TAG, "Error: ${e.message} / ${e.cause}")
                AuthResult.Error(AuthError(e.message ?: "Unknown error"))
            }
        }
    }

    override suspend fun signUp(info: SignUpRequest): AuthResult {
        return try {
            val httpResponse = client.post {
                url(signUpUrl)
                setBody(info)
            }

            if (httpResponse.status.value in 200..299) {
                AuthResult.Authorized(LoginRequest(info.email, info.password))
            } else {
                handleErrors(httpResponse)
            }
        } catch (e: Exception) {
            if (e is CancellationException) {
                throw e
            } else {
                Log.e(TAG, "Error: ${e.message} / ${e.cause}")
                AuthResult.Error(AuthError(e.message ?: "Unknown error"))
            }
        }
    }

    private suspend fun handleErrors(httpResponse: HttpResponse) =
        if (httpResponse.status.value == 401) {
            val message = httpResponse.body<AuthError>()
            AuthResult.Unauthorized(message)
        } else {
            val message = httpResponse.body<AuthError>()
            AuthResult.Error(message)
        }

    private val loginUrl = "${BuildConfig.BASE_URL}/login"
    private val signUpUrl = "${BuildConfig.BASE_URL}/register"

    companion object {
        private const val TAG = "AuthRepository"
    }
}