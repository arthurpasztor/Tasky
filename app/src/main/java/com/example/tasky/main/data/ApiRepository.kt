package com.example.tasky.main.data

import com.example.tasky.auth.domain.Result
import com.example.tasky.auth.domain.RootError
import com.example.tasky.main.data.dto.AgendaDTO
import com.example.tasky.main.domain.AgendaListItem.Reminder
import com.example.tasky.main.domain.AgendaListItem.Task

interface ApiRepository {
    suspend fun authenticate(): Result<Unit, RootError>
    suspend fun logout(): Result<Unit, RootError>

    suspend fun createTask(task: Task): Result<Unit, RootError>
    suspend fun updateTask(task: Task): Result<Unit, RootError>

    suspend fun createReminder(reminder: Reminder): Result<Unit, RootError>
    suspend fun updateReminder(reminder: Reminder): Result<Unit, RootError>

    suspend fun getDailyAgenda(time: Long): Result<AgendaDTO, RootError>
}