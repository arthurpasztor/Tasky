package com.example.tasky.auth.data

import com.example.tasky.BuildConfig
import com.example.tasky.auth.data.dto.LoginDTO
import com.example.tasky.auth.data.dto.SignUpDTO
import com.example.tasky.auth.data.dto.TokenDTO
import com.example.tasky.auth.domain.AuthRepository
import com.example.tasky.core.domain.Result
import com.example.tasky.core.data.Preferences
import com.example.tasky.core.data.executeRequest
import com.example.tasky.core.domain.DataError
import com.example.tasky.core.domain.EmptyResult
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.http.HttpMethod

class AuthRepositoryImpl(private val client: HttpClient, private val prefs: Preferences) : AuthRepository {

    private val loginUrl = "${BuildConfig.BASE_URL}/login"
    private val signUpUrl = "${BuildConfig.BASE_URL}/register"

    override suspend fun login(email: String, password: String): EmptyResult<DataError> {
        return client.executeRequest<LoginDTO, Unit>(
            httpMethod = HttpMethod.Post,
            url = loginUrl,
            payload = LoginDTO(email, password),
            tag = TAG
        ) {
            val response = it.body<TokenDTO>()

            prefs.putEncryptedString(Preferences.KEY_ACCESS_TOKEN, response.accessToken)
            prefs.putEncryptedString(Preferences.KEY_REFRESH_TOKEN, response.refreshToken)
            prefs.putString(Preferences.KEY_USER_NAME, response.fullName)
            prefs.putEncryptedString(Preferences.KEY_USER_ID, response.userId)

            Result.Success(Unit)
        }
    }

    override suspend fun signUp(fullName: String, email: String, password: String): Result<Pair<String, String>, DataError> {
        val result =  client.executeRequest<SignUpDTO, LoginDTO>(
            httpMethod = HttpMethod.Post,
            url = signUpUrl,
            payload = SignUpDTO(fullName, email, password),
            tag = TAG
        ) {
            Result.Success(LoginDTO(email, password))
        }

        return when (result) {
            is Result.Success -> Result.Success(Pair(result.data.email, result.data.password))
            is Result.Error -> result
        }
    }

    companion object {
        private const val TAG = "AuthRepository"
    }
}