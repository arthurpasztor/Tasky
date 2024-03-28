package com.example.tasky.auth.domain

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.example.tasky.R
import io.ktor.client.statement.HttpResponse

sealed class UiText {
    data class DynamicString(val value: String) : UiText()
    class StringResource(
        @StringRes val id: Int,
        val args: Array<Any> = arrayOf()
    ) : UiText()

    @Composable
    fun asString(): String {
        return when (this) {
            is DynamicString -> value
            is StringResource -> LocalContext.current.getString(id, *args)
        }
    }

    fun asString(context: Context): String {
        return when (this) {
            is DynamicString -> value
            is StringResource -> context.getString(id, *args)
        }
    }
}

fun HttpError.asUiText(): UiText {
    return when (this) {
        HttpError.REDIRECT -> UiText.StringResource(R.string.error_redirect)
        HttpError.UNAUTHORIZED -> UiText.StringResource(R.string.error_unauthorized)
        HttpError.REQUEST_TIMEOUT -> UiText.StringResource(R.string.error_request_timed_out)
        HttpError.CONFLICT -> UiText.StringResource(R.string.error_conflict)
        HttpError.PAYLOAD_TOO_LARGE -> UiText.StringResource(R.string.error_payload_too_large)
        HttpError.CLIENT_REQUEST -> UiText.StringResource(R.string.error_client)
        HttpError.SERVER_RESPONSE -> UiText.StringResource(R.string.error_server)
        HttpError.UNKNOWN -> UiText.StringResource(R.string.error_unknown)
    }
}

fun HttpResponse.isSuccess(): Boolean = status.value in 200..299