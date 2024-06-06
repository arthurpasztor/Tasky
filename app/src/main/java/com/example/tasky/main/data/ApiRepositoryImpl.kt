package com.example.tasky.main.data

import com.example.tasky.BuildConfig
import com.example.tasky.auth.domain.Result
import com.example.tasky.auth.domain.RootError
import com.example.tasky.core.data.executeRequest
import com.example.tasky.main.data.dto.AgendaDTO
import com.example.tasky.main.data.dto.ReminderDTO
import com.example.tasky.main.data.dto.TaskDTO
import com.example.tasky.main.domain.AgendaListItem.Reminder
import com.example.tasky.main.domain.AgendaListItem.Task
import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerAuthProvider
import io.ktor.client.plugins.plugin
import io.ktor.client.call.body
import io.ktor.http.HttpMethod

class ApiRepositoryImpl(private val client: HttpClient) : ApiRepository {

    private val tokenCheckUrl = "${BuildConfig.BASE_URL}/authenticate"
    private val logoutUrl = "${BuildConfig.BASE_URL}/logout"

    private val agendaUrl = "${BuildConfig.BASE_URL}/agenda"
    private val taskUrl = "${BuildConfig.BASE_URL}/task"
    private val reminderUrl = "${BuildConfig.BASE_URL}/reminder"

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
        client.plugin(Auth).providers.filterIsInstance<BearerAuthProvider>().firstOrNull()?.clearToken()

        return client.executeRequest<Unit, Unit>(
            httpMethod = HttpMethod.Get,
            url = logoutUrl,
            tag = TAG
        ) {
            Result.Success(Unit)
        }
    }

    override suspend fun createTask(task: Task): Result<Unit, RootError> {
        return client.executeRequest<TaskDTO, Unit>(
            httpMethod = HttpMethod.Post,
            url = taskUrl,
            payload = TaskDTO(task),
            tag = TAG
        ) {
            Result.Success(Unit)
        }
    }

    override suspend fun updateTask(task: Task): Result<Unit, RootError> {
        TODO("Not yet implemented")
    }

    override suspend fun createReminder(reminder: Reminder): Result<Unit, RootError> {
        return client.executeRequest<ReminderDTO, Unit>(
            httpMethod = HttpMethod.Post,
            url = reminderUrl,
            payload = ReminderDTO(reminder),
            tag = TAG
        ) {
            Result.Success(Unit)
        }
    }

    override suspend fun updateReminder(reminder: Reminder): Result<Unit, RootError> {
        TODO("Not yet implemented")
    }

    override suspend fun getDailyAgenda(time: Long): Result<AgendaDTO, RootError> {
        return client.executeRequest<Unit, AgendaDTO>(
            httpMethod = HttpMethod.Get,
            url = agendaUrl,
            queryParams = Pair(QUERY_PARAM_KEY_TIME, time),
            tag = TAG
        ) {
            Result.Success(it.body())
        }
    }

    companion object {
        private const val TAG = "ApiRepository"

        private const val QUERY_PARAM_KEY_TIME = "time"
    }
}