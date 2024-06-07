package com.example.tasky.agenda.data

import com.example.tasky.BuildConfig
import com.example.tasky.agenda.domain.AuthRepository
import com.example.tasky.core.data.executeRequest
import com.example.tasky.core.domain.DataError
import com.example.tasky.core.domain.Result
import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerAuthProvider
import io.ktor.client.plugins.plugin
import io.ktor.http.HttpMethod

class AuthRepositoryImpl(private val client: HttpClient) : AuthRepository {

    private val tokenCheckUrl = "${BuildConfig.BASE_URL}/authenticate"
    private val logoutUrl = "${BuildConfig.BASE_URL}/logout"

    override suspend fun authenticate(): Result<Unit, DataError> {
        return client.executeRequest<Unit, Unit>(
            httpMethod = HttpMethod.Get,
            url = tokenCheckUrl,
            tag = TAG
        ) {
            Result.Success(Unit)
        }
    }

    override suspend fun logout(): Result<Unit, DataError> {
        client.plugin(Auth).providers.filterIsInstance<BearerAuthProvider>().firstOrNull()?.clearToken()

        return client.executeRequest<Unit, Unit>(
            httpMethod = HttpMethod.Get,
            url = logoutUrl,
            tag = TAG
        ) {
            Result.Success(Unit)
        }
    }

    companion object {
        private const val TAG = "ApiRepository"
    }
}