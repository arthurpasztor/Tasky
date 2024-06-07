package com.example.tasky.agenda.domain

import com.example.tasky.core.domain.Result
import com.example.tasky.core.domain.RootError
import com.example.tasky.agenda.domain.AgendaListItem.TaskDM

interface TaskRepository {
    suspend fun createTask(task: TaskDM): Result<Unit, RootError>
    suspend fun updateTask(task: TaskDM): Result<Unit, RootError>
}