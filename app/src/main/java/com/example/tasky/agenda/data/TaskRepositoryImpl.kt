package com.example.tasky.agenda.data

import com.example.tasky.BuildConfig
import com.example.tasky.agenda.data.dto.TaskDTO
import com.example.tasky.agenda.data.dto.toTaskDTO
import com.example.tasky.agenda.domain.TaskRepository
import com.example.tasky.agenda.domain.model.AgendaListItem
import com.example.tasky.core.data.executeRequest
import com.example.tasky.core.domain.DataError
import com.example.tasky.core.domain.Result
import io.ktor.client.HttpClient
import io.ktor.http.HttpMethod

class TaskRepositoryImpl(private val client: HttpClient) : TaskRepository {

    private val taskUrl = "${BuildConfig.BASE_URL}/task"

    override suspend fun createTask(task: AgendaListItem.Task): Result<Unit, DataError> {
        return client.executeRequest<TaskDTO, Unit>(
            httpMethod = HttpMethod.Post,
            url = taskUrl,
            payload = task.toTaskDTO(),
            tag = TAG
        ) {
            Result.Success(Unit)
        }
    }

    override suspend fun updateTask(task: AgendaListItem.Task): Result<Unit, DataError> {
        TODO("Not yet implemented")
    }

    companion object {
        private const val TAG = "TaskRepository"
    }
}