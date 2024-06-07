package com.example.tasky.auth.data

import com.example.tasky.BuildConfig
import com.example.tasky.auth.data.dto.LoginDTO
import com.example.tasky.auth.data.dto.SignUpDTO
import com.example.tasky.auth.data.dto.TokenDTO
import com.example.tasky.auth.data.dto.toLogin
import com.example.tasky.auth.data.dto.toLoginDTO
import com.example.tasky.auth.data.dto.toSignUpDTO
import com.example.tasky.auth.domain.AuthRepository
import com.example.tasky.auth.domain.Login
import com.example.tasky.auth.domain.SignUp
import com.example.tasky.core.domain.Result
import com.example.tasky.core.domain.RootError
import com.example.tasky.core.data.Preferences
import com.example.tasky.core.data.executeRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.http.HttpMethod

class AuthRepositoryImpl(private val client: HttpClient, private val prefs: Preferences) : AuthRepository {

    private val loginUrl = "${BuildConfig.BASE_URL}/login"
    private val signUpUrl = "${BuildConfig.BASE_URL}/register"

    override suspend fun login(info: Login): Result<Unit, RootError> {
        return client.executeRequest<LoginDTO, Unit>(
            httpMethod = HttpMethod.Post,
            url = loginUrl,
            payload = info.toLoginDTO(),
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

    override suspend fun signUp(info: SignUp): Result<Login, RootError> {
        val result =  client.executeRequest<SignUpDTO, LoginDTO>(
            httpMethod = HttpMethod.Post,
            url = signUpUrl,
            payload = info.toSignUpDTO(),
            tag = TAG
        ) {
            Result.Success(LoginDTO(info.email, info.password))
        }

        return when (result) {
            is Result.Success -> Result.Success(result.data.toLogin())
            is Result.Error -> result
        }
    }

    companion object {
        private const val TAG = "AuthRepository"
    }
}