package com.example.tasky.main.data

import com.example.tasky.BuildConfig
import com.example.tasky.auth.domain.Result
import com.example.tasky.auth.domain.RootError
import com.example.tasky.core.data.BaseRepositoryImpl
import io.ktor.client.HttpClient
import io.ktor.http.HttpMethod

class ApiRepositoryImpl(client: HttpClient) : ApiRepository, BaseRepositoryImpl(client) {

    private val tokenCheckUrl = "${BuildConfig.BASE_URL}/authenticate"
    private val logoutUrl = "${BuildConfig.BASE_URL}/logout"

    override suspend fun authenticate(): Result<Unit, RootError> {
        return executeRequest<Unit, Unit>(HttpMethod.Get, tokenCheckUrl) {
            Result.Success(Unit)
        }
    }

    override suspend fun logout(): Result<Unit, RootError> {
        return executeRequest<Unit, Unit>(HttpMethod.Get, logoutUrl) {
            Result.Success(Unit)
        }
    }

    override fun tag() = TAG

    companion object {
        private const val TAG = "ApiRepository"
    }
}