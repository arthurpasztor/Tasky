package com.example.tasky.auth.domain

import com.example.tasky.core.domain.Result
import com.example.tasky.core.domain.RootError

interface AuthRepository {
    suspend fun login(info: LoginDM): Result<Unit, RootError>
    suspend fun signUp(info: SignUpDM): Result<LoginDM, RootError>
}
