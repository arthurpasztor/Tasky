package com.example.tasky.main.data

import com.example.tasky.auth.domain.Result
import com.example.tasky.auth.domain.RootError
import com.example.tasky.main.data.dto.TaskDTO

interface ApiRepository {
    suspend fun authenticate(): Result<Unit, RootError>
    suspend fun logout(): Result<Unit, RootError>

    suspend fun createTask(task: TaskDTO): Result<Unit, RootError>
    suspend fun updateTask(task: TaskDTO): Result<Unit, RootError>
}