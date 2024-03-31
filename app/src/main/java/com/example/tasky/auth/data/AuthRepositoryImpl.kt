package com.example.tasky.auth.data

import com.example.tasky.BuildConfig
import com.example.tasky.auth.data.dto.LoginRequest
import com.example.tasky.auth.data.dto.SignUpRequest
import com.example.tasky.auth.data.dto.TokenResponse
import com.example.tasky.auth.domain.Result
import com.example.tasky.auth.domain.RootError
import com.example.tasky.core.data.BaseRepositoryImpl
import com.example.tasky.core.data.Preferences
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.http.HttpMethod
import org.koin.java.KoinJavaComponent.inject

class AuthRepositoryImpl(client: HttpClient) : AuthRepository, BaseRepositoryImpl(client) {

    private val prefs: Preferences by inject(Preferences::class.java)

    private val loginUrl = "${BuildConfig.BASE_URL}/login"
    private val signUpUrl = "${BuildConfig.BASE_URL}/register"

    override suspend fun login(info: LoginRequest): Result<Unit, RootError> {
        return executeRequest<LoginRequest, Unit>(HttpMethod.Post, loginUrl, info) {
            val response = it.body<TokenResponse>()

            prefs.putEncryptedString(Preferences.KEY_TOKEN, response.token)

            Result.Success(Unit)
        }
    }

    override suspend fun signUp(info: SignUpRequest): Result<LoginRequest, RootError> {
        return executeRequest<SignUpRequest, LoginRequest>(HttpMethod.Post, signUpUrl, info) {
            Result.Success(LoginRequest(info.email, info.password))
        }
    }

    override fun tag() = TAG

    companion object {
        private const val TAG = "AuthRepository"
    }
}