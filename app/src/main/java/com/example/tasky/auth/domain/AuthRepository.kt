package com.example.tasky.auth.domain

import com.example.tasky.core.domain.Result
import com.example.tasky.core.domain.RootError

interface AuthRepository {
    suspend fun login(info: Login): Result<Unit, RootError>
    suspend fun signUp(info: SignUp): Result<Login, RootError>
}
