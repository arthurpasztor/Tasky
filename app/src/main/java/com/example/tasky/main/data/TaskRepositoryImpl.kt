package com.example.tasky.main.data

import com.example.tasky.BuildConfig
import com.example.tasky.core.data.executeRequest
import com.example.tasky.core.domain.Result
import com.example.tasky.core.domain.RootError
import com.example.tasky.main.data.dto.TaskDTO
import com.example.tasky.main.data.dto.toTaskDTO
import com.example.tasky.main.domain.AgendaListItem
import com.example.tasky.main.domain.TaskRepository
import io.ktor.client.HttpClient
import io.ktor.http.HttpMethod

class TaskRepositoryImpl(private val client: HttpClient) : TaskRepository {

    private val taskUrl = "${BuildConfig.BASE_URL}/task"

    override suspend fun createTask(task: AgendaListItem.TaskDM): Result<Unit, RootError> {
        return client.executeRequest<TaskDTO, Unit>(
            httpMethod = HttpMethod.Post,
            url = taskUrl,
            payload = task.toTaskDTO(),
            tag = TAG
        ) {
            Result.Success(Unit)
        }
    }

    override suspend fun updateTask(task: AgendaListItem.TaskDM): Result<Unit, RootError> {
        TODO("Not yet implemented")
    }

    companion object {
        private const val TAG = "TaskRepository"
    }
}