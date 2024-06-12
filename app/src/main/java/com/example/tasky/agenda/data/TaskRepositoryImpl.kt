package com.example.tasky.agenda.data

import com.example.tasky.BuildConfig
import com.example.tasky.agenda.data.dto.TaskDTO
import com.example.tasky.agenda.data.dto.toTaskDTO
import com.example.tasky.agenda.domain.TaskRepository
import com.example.tasky.agenda.domain.model.AgendaListItem
import com.example.tasky.core.data.executeRequest
import com.example.tasky.core.domain.DataError
import com.example.tasky.core.domain.EmptyResult
import com.example.tasky.core.domain.Result
import io.ktor.client.HttpClient
import io.ktor.http.HttpMethod

class TaskRepositoryImpl(private val client: HttpClient) : TaskRepository {

    private val taskUrl = "${BuildConfig.BASE_URL}/task"

    override suspend fun createTask(task: AgendaListItem.Task): EmptyResult<DataError> {
        return client.executeRequest<TaskDTO, Unit>(
            httpMethod = HttpMethod.Post,
            url = taskUrl,
            payload = task.toTaskDTO(),
            tag = TAG
        ) {
            Result.Success(Unit)
        }
    }

    override suspend fun updateTask(task: AgendaListItem.Task): EmptyResult<DataError> {
        return client.executeRequest<TaskDTO, Unit>(
            httpMethod = HttpMethod.Put,
            url = taskUrl,
            payload = task.toTaskDTO(),
            tag = TAG
        ) {
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
            Result.Success(Unit)
        }
    }

    companion object {
        private const val TAG = "TaskRepository"

        private const val QUERY_PARAM_KEY_ID = "taskId"
    }
}