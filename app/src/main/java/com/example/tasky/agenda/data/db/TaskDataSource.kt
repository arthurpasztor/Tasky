package com.example.tasky.agenda.data.db

import com.example.tasky.agenda.data.dto.TaskDTO
import com.example.tasky.agenda.domain.model.OfflineStatus
import com.example.tasky.migrations.TaskEntity

interface TaskDataSource {

    suspend fun getTaskById(id: String): TaskEntity?

    suspend fun getAllOfflineTasks(offlineUserAuthorId: String, offlineStatus: OfflineStatus): List<TaskEntity>

    suspend fun getAllTasks(): List<TaskEntity>

    suspend fun insertOrReplaceTask(
        task: TaskDTO,
        offlineUserAuthorId: String? = null,
        offlineStatus: OfflineStatus? = null
    )

    suspend fun deleteTask(id: String)

    suspend fun deleteAllTasks()
}