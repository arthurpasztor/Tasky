package com.example.tasky.agenda.data.db

import com.example.tasky.agenda.data.dto.TaskDTO
import com.example.tasky.agenda.domain.getFormattedLocalDateFromMillis
import com.example.tasky.agenda.domain.model.OfflineStatus
import com.example.tasky.db.TaskyDatabase
import com.example.tasky.migrations.TaskEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TaskDataSourceImpl(db: TaskyDatabase) : TaskDataSource {

    private val queries = db.taskEntityQueries

    override suspend fun getTaskById(id: String): TaskEntity? {
        return withContext(Dispatchers.IO) {
            queries.getTaskById(id).executeAsOneOrNull()
        }
    }

    override suspend fun getAllOfflineTasks(offlineUserAuthorId: String, offlineStatus: OfflineStatus): List<TaskEntity> {
        return withContext(Dispatchers.IO) {
            queries.getAllOfflineTasks(offlineUserAuthorId, offlineStatus).executeAsList()
        }
    }

    override suspend fun getAllTasks(): List<TaskEntity> {
        return withContext(Dispatchers.IO) {
            queries.getAllTasks().executeAsList()
        }
    }

    override suspend fun insertOrReplaceTask(
        task: TaskDTO,
        offlineUserAuthorId: String?,
        offlineStatus: OfflineStatus?
    ) {
        withContext(Dispatchers.IO) {
            queries.insertOrReplaceTask(
                task.id,
                task.title,
                task.description,
                task.time,
                task.remindAt,
                task.isDone,
                task.time.getFormattedLocalDateFromMillis(),
                offlineUserAuthorId,
                offlineStatus
            )
        }
    }

    override suspend fun deleteTask(id: String) {
        withContext(Dispatchers.IO) {
            queries.deleteTaskById(id)
        }
    }

    override suspend fun deleteAllTasks() {
        withContext(Dispatchers.IO) {
            queries.deleteAll(null)
        }
    }
}