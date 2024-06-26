package com.example.tasky.agenda.data

import com.example.tasky.BuildConfig
import com.example.tasky.agenda.data.db.TaskDataSource
import com.example.tasky.agenda.data.dto.TaskDTO
import com.example.tasky.agenda.data.dto.toTask
import com.example.tasky.agenda.data.dto.toTaskDTO
import com.example.tasky.agenda.domain.TaskRepository
import com.example.tasky.agenda.domain.model.AgendaListItem.Task
import com.example.tasky.core.data.executeRequest
import com.example.tasky.core.domain.DataError
import com.example.tasky.core.domain.EmptyResult
import com.example.tasky.core.domain.Result
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.http.HttpMethod

class TaskRepositoryImpl(private val client: HttpClient, private val localDataSource: TaskDataSource) : TaskRepository {

    private val taskUrl = "${BuildConfig.BASE_URL}/task"

    override suspend fun createTask(task: Task): EmptyResult<DataError> {
        val taskDTO = task.toTaskDTO()

        return client.executeRequest<TaskDTO, Unit>(
            httpMethod = HttpMethod.Post,
            url = taskUrl,
            payload = taskDTO,
            tag = TAG
        ) {
            localDataSource.insertOrReplaceTask(taskDTO)
            Result.Success(Unit)
        }
    }

    override suspend fun updateTask(task: Task): EmptyResult<DataError> {
        val taskDTO = task.toTaskDTO()

        return client.executeRequest<TaskDTO, Unit>(
            httpMethod = HttpMethod.Put,
            url = taskUrl,
            payload = taskDTO,
            tag = TAG
        ) {
            localDataSource.insertOrReplaceTask(taskDTO)
            Result.Success(Unit)
        }
    }

    override suspend fun deleteTask(taskId: String): EmptyResult<DataError> {
        return client.executeRequest<Unit, Unit>(
            httpMethod = HttpMethod.Delete,
            url = taskUrl,
            queryParams = Pair(QUERY_PARAM_KEY_ID, taskId),
            tag = TAG
        ) {
            localDataSource.deleteTask(taskId)
            Result.Success(Unit)
        }
    }

    override suspend fun getTaskDetails(taskId: String): Result<Task, DataError> {
        val result = client.executeRequest<Unit, TaskDTO>(
            httpMethod = HttpMethod.Get,
            url = taskUrl,
            queryParams = Pair(QUERY_PARAM_KEY_ID, taskId),
            tag = TAG
        ) {
            Result.Success(it.body())
        }

        return when (result) {
            is Result.Success -> {
                localDataSource.insertOrReplaceTask(result.data)
                Result.Success(result.data.toTask())
            }

            is Result.Error -> Result.Error(result.error)
        }
    }

    companion object {
        private const val TAG = "TaskRepository"

        private const val QUERY_PARAM_KEY_ID = "taskId"
    }
}