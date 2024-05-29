package com.example.tasky.core.data

import android.util.Log
import com.example.tasky.BuildConfig
import com.example.tasky.auth.domain.isSuccess
import com.example.tasky.core.data.dto.AccessTokenRequest
import com.example.tasky.core.data.dto.AccessTokenResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.URLProtocol
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.java.KoinJavaComponent.inject

object HttpClientFactory {

    private const val TIME_OUT = 5000

    private val prefs: Preferences by inject(Preferences::class.java)

    fun provideAuthClient() = HttpClient {

        install(Auth) {
            bearer {
                loadTokens {
                    BearerTokens(
                        accessToken = prefs.getEncryptedString(Preferences.KEY_ACCESS_TOKEN, ""),
                        refreshToken = prefs.getEncryptedString(Preferences.KEY_REFRESH_TOKEN, "")
                    )
                }
                refreshTokens {
                    val refreshToken = prefs.getEncryptedString(Preferences.KEY_REFRESH_TOKEN, "")
                    val userId = prefs.getEncryptedString(Preferences.KEY_USER_ID, "")
                    val response: HttpResponse = client.post("${BuildConfig.BASE_URL}/accessToken") {
                        setBody(AccessTokenRequest(refreshToken = refreshToken, userId = userId))
                    }

                    if (response.isSuccess()) {
                        val data = response.body<AccessTokenResponse>()
                        prefs.putEncryptedString(Preferences.KEY_ACCESS_TOKEN, data.accessToken)

                        BearerTokens(
                            accessToken = data.accessToken,
                            refreshToken = refreshToken
                        )
                    } else {
                        BearerTokens("", "")
                    }
                }
            }
        }

        install(DefaultRequest) {
            contentType(ContentType.Application.Json)
            header("x-api-key", BuildConfig.API_KEY)
        }

        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    Log.d("Auth HttpClient", "log: $message")
                }
            }
            level = LogLevel.ALL
        }

        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
    }

    fun provideApiClient() = HttpClient(Android) {

        val accessToken = prefs.getEncryptedString(Preferences.KEY_ACCESS_TOKEN, "")

        install(DefaultRequest) {
            contentType(ContentType.Application.Json)
            header("x-api-key", BuildConfig.API_KEY)
            header("Authorization", "bearer $accessToken")
        }

        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    Log.d("Auth HttpClient", "log: $message")
                }
            }
            level = LogLevel.ALL
        }
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
        engine {
            connectTimeout = TIME_OUT
            socketTimeout = TIME_OUT
        }

        engine {
            connectTimeout = TIME_OUT
            socketTimeout = TIME_OUT
        }
    }
}