package com.example.tasky.main.data

import com.example.tasky.BuildConfig
import com.example.tasky.auth.domain.Result
import com.example.tasky.auth.domain.RootError
import com.example.tasky.core.data.executeRequest
import com.example.tasky.main.data.dto.TaskDTO
import io.ktor.client.HttpClient
import io.ktor.http.HttpMethod

class ApiRepositoryImpl(private val client: HttpClient) : ApiRepository {

    private val tokenCheckUrl = "${BuildConfig.BASE_URL}/authenticate"
    private val logoutUrl = "${BuildConfig.BASE_URL}/logout"

    private val taskUrl = "${BuildConfig.BASE_URL}/task"

    override suspend fun authenticate(): Result<Unit, RootError> {
        return client.executeRequest<Unit, Unit>(
            httpMethod = HttpMethod.Get,
            url = tokenCheckUrl,
            tag = TAG
        ) {
            Result.Success(Unit)
        }
    }

    override suspend fun logout(): Result<Unit, RootError> {
        return client.executeRequest<Unit, Unit>(
            httpMethod = HttpMethod.Get,
            url = logoutUrl,
            tag = TAG
        ) {
            Result.Success(Unit)
        }
    }

    override suspend fun createTask(task: TaskDTO): Result<Unit, RootError> {
        return client.executeRequest<TaskDTO, Unit>(
            httpMethod = HttpMethod.Post,
            url = taskUrl,
            payload = task,
            tag = TAG
        ) {
            Result.Success(Unit)
        }
    }

    override suspend fun updateTask(task: TaskDTO): Result<Unit, RootError> {
        TODO("Not yet implemented")
    }

    companion object {
        private const val TAG = "ApiRepository"
    }
}