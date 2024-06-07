package com.example.tasky.agenda.domain

import com.example.tasky.core.domain.Result
import com.example.tasky.core.domain.RootError

interface AuthRepository {
    suspend fun authenticate(): Result<Unit, RootError>
    suspend fun logout(): Result<Unit, RootError>
}