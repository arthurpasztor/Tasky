package com.example.tasky.auth.data

import android.util.Log
import com.example.tasky.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object NetworkWrapper {

    private const val TIME_OUT = 5000

    fun provideAuthClient() = HttpClient(Android) {

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

        engine {
            connectTimeout= TIME_OUT
            socketTimeout= TIME_OUT
        }
    }
}