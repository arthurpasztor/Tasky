package com.example.tasky.main.domain

import com.example.tasky.core.domain.Result
import com.example.tasky.core.domain.RootError
import com.example.tasky.main.domain.AgendaListItem.ReminderDM
import com.example.tasky.main.domain.AgendaListItem.TaskDM

interface ApiRepository {
    suspend fun authenticate(): Result<Unit, RootError>
    suspend fun logout(): Result<Unit, RootError>

    suspend fun createTask(task: TaskDM): Result<Unit, RootError>
    suspend fun updateTask(task: TaskDM): Result<Unit, RootError>

    suspend fun createReminder(reminder: ReminderDM): Result<Unit, RootError>
    suspend fun updateReminder(reminder: ReminderDM): Result<Unit, RootError>

    suspend fun getDailyAgenda(time: Long): Result<AgendaDM, RootError>
}