package com.example.tasky.core.domain

interface Error

sealed interface DataError : Error {
    enum class HttpError : DataError {
        REDIRECT, //3xx
        UNAUTHORIZED, //401
        REQUEST_TIMEOUT, //408
        CONFLICT_LOGIN, //409
        CONFLICT_SIGN_UP, //409
        PAYLOAD_TOO_LARGE, //413
        CLIENT_REQUEST, //4xx
        SERVER_RESPONSE, //5xx
        UNKNOWN
    }

    enum class LocalError : DataError {
        DISK_FULL,
        NOT_FOUND
    }
}
