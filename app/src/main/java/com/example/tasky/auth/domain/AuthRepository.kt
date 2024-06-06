package com.example.tasky.auth.domain

import com.example.tasky.auth.data.dto.LoginRequest
import com.example.tasky.auth.data.dto.SignUpRequest
import com.example.tasky.core.domain.Result
import com.example.tasky.core.domain.RootError

interface AuthRepository {
    suspend fun login(info: LoginRequest): Result<Unit, RootError>
    suspend fun signUp(info: SignUpRequest): Result<LoginRequest, RootError>
}
