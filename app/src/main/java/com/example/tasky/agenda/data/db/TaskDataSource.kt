package com.example.tasky.agenda.data.db

import com.example.tasky.agenda.data.dto.TaskDTO
import com.example.tasky.migrations.TaskEntity
import kotlinx.coroutines.flow.Flow

interface TaskDataSource {

    suspend fun getTaskById(id: String): TaskEntity?

    suspend fun getAllTasks(): Flow<List<TaskEntity>>

    suspend fun insertOrReplaceTask(task: TaskDTO)

    suspend fun deleteTask(id: String)

    suspend fun deleteAllTasks()
}