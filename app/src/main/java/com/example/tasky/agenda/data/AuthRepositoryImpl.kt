package com.example.tasky.agenda.data

import com.example.tasky.BuildConfig
import com.example.tasky.agenda.data.db.AgendaDataSource
import com.example.tasky.agenda.domain.AuthRepository
import com.example.tasky.agenda.domain.NetworkConnectivityMonitor
import com.example.tasky.core.data.Preferences
import com.example.tasky.core.data.executeRequest
import com.example.tasky.core.domain.DataError
import com.example.tasky.core.domain.EmptyResult
import com.example.tasky.core.domain.Result
import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerAuthProvider
import io.ktor.client.plugins.plugin
import io.ktor.http.HttpMethod
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class AuthRepositoryImpl(
    private val client: HttpClient,
    private val localDataSource: AgendaDataSource,
    private val applicationScope: CoroutineScope,
    private val networkMonitor: NetworkConnectivityMonitor,
    private val prefs: Preferences
) : AuthRepository {

    private val tokenCheckUrl = "${BuildConfig.BASE_URL}/authenticate"
    private val logoutUrl = "${BuildConfig.BASE_URL}/logout"

    override suspend fun authenticate(): EmptyResult<DataError> {
        return if (networkMonitor.isNetworkAvailable()) {
            client.executeRequest<Unit, Unit>(
                httpMethod = HttpMethod.Get,
                url = tokenCheckUrl,
                tag = TAG
            ) {
                Result.Success(Unit)
            }
        } else {
            if (prefs.containsEncrypted(Preferences.KEY_ACCESS_TOKEN)) {
                Result.Success(Unit)
            } else {
                Result.Error(DataError.LocalError.USER_IS_LOGGED_OUT)
            }
        }
    }

    override suspend fun logout(): EmptyResult<DataError> {
        //TODO handle offline use case
        client.plugin(Auth).providers.filterIsInstance<BearerAuthProvider>().firstOrNull()?.clearToken()

        return client.executeRequest<Unit, Unit>(
            httpMethod = HttpMethod.Get,
            url = logoutUrl,
            tag = TAG
        ) {
            applicationScope.launch {
                localDataSource.clearDatabase()
            }.join()

            Result.Success(Unit)
        }
    }

    companion object {
        private const val TAG = "ApiRepository"
    }
}