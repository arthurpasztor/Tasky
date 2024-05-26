package com.example.tasky.auth.data

import com.example.tasky.BuildConfig
import com.example.tasky.auth.data.dto.LoginRequest
import com.example.tasky.auth.data.dto.SignUpRequest
import com.example.tasky.auth.data.dto.TokenResponse
import com.example.tasky.auth.domain.Result
import com.example.tasky.auth.domain.RootError
import com.example.tasky.core.data.Preferences
import com.example.tasky.core.data.executeRequest
import com.example.tasky.main.di.apiModule
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.http.HttpMethod
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules

class AuthRepositoryImpl(private val client: HttpClient, private val prefs: Preferences) : AuthRepository {

    private val loginUrl = "${BuildConfig.BASE_URL}/login"
    private val signUpUrl = "${BuildConfig.BASE_URL}/register"

    override suspend fun login(info: LoginRequest): Result<Unit, RootError> {
        return client.executeRequest<LoginRequest, Unit>(
            httpMethod = HttpMethod.Post,
            url = loginUrl,
            payload = info,
            tag = TAG
        ) {
            val response = it.body<TokenResponse>()

            prefs.putEncryptedString(Preferences.KEY_ACCESS_TOKEN, response.accessToken)
            prefs.putEncryptedString(Preferences.KEY_REFRESH_TOKEN, response.refreshToken)
            prefs.putEncryptedLong(Preferences.KEY_ACCESS_TOKEN_EXPIRATION_TIME, response.accessTokenExpirationTimestamp)
            prefs.putString(Preferences.KEY_USER_NAME, response.fullName)

            unloadKoinModules(apiModule)
            loadKoinModules(apiModule)

            Result.Success(Unit)
        }
    }

    override suspend fun signUp(info: SignUpRequest): Result<LoginRequest, RootError> {
        return client.executeRequest<SignUpRequest, LoginRequest>(
            httpMethod = HttpMethod.Post,
            url = signUpUrl,
            payload = info,
            tag = TAG
        ) {
            Result.Success(LoginRequest(info.email, info.password))
        }
    }

    companion object {
        private const val TAG = "AuthRepository"
    }
}