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

    override suspend fun insertOrReplaceTasks(tasks: List<TaskDTO>) {
        tasks.forEach {
            insertOrReplaceTask(it)
        }
    }

    override suspend fun insertOrReplaceTask(task: TaskDTO) {
        insertOrReplaceTask(task.id, task.title, task.description, task.time, task.remindAt, task.isDone)
    }

    private suspend fun insertOrReplaceTask(
        id: String,
        title: String,
        description: String,
        time: Long,
        remindAt: Long,
        isDone: Boolean
    ) {
        withContext(Dispatchers.IO) {
            queries.insertOrReplaceTask(id, title, description, time, remindAt, isDone)
        }
    }

    override suspend fun deleteTask(id: String) {
        withContext(Dispatchers.IO) {
            queries.deleteTaskById(id)
        }
    }
}