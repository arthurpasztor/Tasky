package com.example.tasky.auth.domain

import com.example.tasky.core.domain.Result
import com.example.tasky.core.domain.RootError

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<Unit, RootError>
    suspend fun signUp(fullName: String, email: String, password: String): Result<Pair<String, String>, RootError>
}
