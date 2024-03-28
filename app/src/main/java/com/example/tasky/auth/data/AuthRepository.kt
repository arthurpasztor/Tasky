package com.example.tasky.auth.data

import com.example.tasky.auth.data.dto.LoginRequest
import com.example.tasky.auth.data.dto.SignUpRequest
import com.example.tasky.auth.domain.Result
import com.example.tasky.auth.domain.RootError

interface AuthRepository {
    suspend fun login(info: LoginRequest): Result<Unit, RootError>
    suspend fun signUp(info: SignUpRequest): Result<LoginRequest, RootError>
}
