package com.example.tasky.agenda.domain

import com.example.tasky.core.domain.DataError
import com.example.tasky.core.domain.EmptyResult

interface AuthRepository {
    suspend fun authenticate(): EmptyResult<DataError>
    suspend fun logout(): EmptyResult<DataError>
}