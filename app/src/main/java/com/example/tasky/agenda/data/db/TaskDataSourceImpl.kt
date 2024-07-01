package com.example.tasky.agenda.data.db

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.example.tasky.agenda.data.dto.TaskDTO
import com.example.tasky.db.TaskEntity
import com.example.tasky.db.TaskyDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class TaskDataSourceImpl(db: TaskyDatabase) : TaskDataSource {

    private val queries = db.taskEntityQueries

    override suspend fun getTaskById(id: String): TaskEntity? {
        return withContext(Dispatchers.IO) {
            queries.getTaskById(id).executeAsOneOrNull()
        }
    }

    override suspend fun getAllTasks(): Flow<List<TaskEntity>> {
        return withContext(Dispatchers.IO) {
            queries.getAllTasks().asFlow().mapToList(this.coroutineContext)
        }
    }

    override suspend fun insertOrReplaceTask(task: TaskDTO) {
        withContext(Dispatchers.IO) {
            queries.insertOrReplaceTask(task.id, task.title, task.description, task.time, task.remindAt, task.isDone)
        }
    }

    override suspend fun deleteTask(id: String) {
        withContext(Dispatchers.IO) {
            queries.deleteTaskById(id)
        }
    }

    override suspend fun deleteAllTasks() {
        withContext(Dispatchers.IO) {
            queries.deleteAll()
        }
    }
}