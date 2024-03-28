package com.example.tasky.auth.data

import com.example.tasky.auth.data.dto.AuthError

sealed class AuthResult {
    class Authorized<T>(val data: T? = null): AuthResult()
    class Unauthorized(val error: AuthError) : AuthResult()
    class Error(val error: AuthError): AuthResult()
}
