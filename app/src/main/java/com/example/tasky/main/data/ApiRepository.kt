package com.example.tasky.main.data

import com.example.tasky.auth.domain.Result
import com.example.tasky.auth.domain.RootError

interface ApiRepository {
    suspend fun authenticate(): Result<Unit, RootError>
}