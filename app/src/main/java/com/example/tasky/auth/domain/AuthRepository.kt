package com.example.tasky.auth.domain

import com.example.tasky.core.domain.DataError
import com.example.tasky.core.domain.EmptyResult
import com.example.tasky.core.domain.Result

interface AuthRepository {
    suspend fun login(email: String, password: String): EmptyResult<DataError>
    suspend fun signUp(fullName: String, email: String, password: String): Result<Pair<String, String>, DataError>
}
