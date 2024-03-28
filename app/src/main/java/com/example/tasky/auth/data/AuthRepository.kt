package com.example.tasky.auth.data

import com.example.tasky.auth.data.dto.LoginRequest
import com.example.tasky.auth.data.dto.SignUpRequest

interface AuthRepository {
    suspend fun login(info: LoginRequest): AuthResult
    suspend fun signUp(info: SignUpRequest): AuthResult
}
