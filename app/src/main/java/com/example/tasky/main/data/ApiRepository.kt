package com.example.tasky.main.data

import com.example.tasky.auth.domain.Result
import com.example.tasky.auth.domain.RootError
import com.example.tasky.main.data.dto.AgendaDTO
import com.example.tasky.main.data.dto.ReminderDTO
import com.example.tasky.main.data.dto.TaskDTO

interface ApiRepository {
    suspend fun authenticate(): Result<Unit, RootError>
    suspend fun logout(): Result<Unit, RootError>

    suspend fun createTask(task: TaskDTO): Result<Unit, RootError>
    suspend fun updateTask(task: TaskDTO): Result<Unit, RootError>

    suspend fun createReminder(reminder: ReminderDTO): Result<Unit, RootError>
    suspend fun updateReminder(reminder: ReminderDTO): Result<Unit, RootError>

    suspend fun getDailyAgenda(time: Long): Result<AgendaDTO, RootError>
}