package com.example.tasky.agenda.domain

import com.example.tasky.core.domain.Result
import com.example.tasky.core.domain.RootError
import com.example.tasky.agenda.domain.model.AgendaListItem.Task

interface TaskRepository {
    suspend fun createTask(task: Task): Result<Unit, RootError>
    suspend fun updateTask(task: Task): Result<Unit, RootError>
}