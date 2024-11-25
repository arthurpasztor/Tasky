package com.example.tasky.agenda.domain

import com.example.tasky.agenda.domain.model.AgendaListItem.Task
import com.example.tasky.core.domain.DataError
import com.example.tasky.core.domain.EmptyResult
import com.example.tasky.core.domain.Result

interface TaskRepository {
    suspend fun createTask(task: Task): EmptyResult<DataError>
    suspend fun updateTask(task: Task): EmptyResult<DataError>
    suspend fun deleteTask(taskId: String): EmptyResult<DataError>
    suspend fun getTaskDetails(taskId: String): Result<Task, DataError>
}