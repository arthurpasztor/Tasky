package com.example.tasky.agenda.domain

import com.example.tasky.agenda.domain.model.AgendaListItem.Task
import com.example.tasky.core.domain.DataError
import com.example.tasky.core.domain.Result

interface TaskRepository {
    suspend fun createTask(task: Task): Result<Unit, DataError>
    suspend fun updateTask(task: Task): Result<Unit, DataError>
}