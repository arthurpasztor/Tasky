package com.example.tasky.agenda.domain

import com.example.tasky.core.domain.DataError
import com.example.tasky.core.domain.Result

interface AuthRepository {
    suspend fun authenticate(): Result<Unit, DataError>
    suspend fun logout(): Result<Unit, DataError>
}