package com.example.tasky.agenda.data

import com.example.tasky.BuildConfig
import com.example.tasky.agenda.data.db.EventDataSource
import com.example.tasky.agenda.data.db.ReminderDataSource
import com.example.tasky.agenda.data.db.TaskDataSource
import com.example.tasky.core.data.executeRequest
import com.example.tasky.core.domain.Result
import com.example.tasky.agenda.data.dto.AgendaDTO
import com.example.tasky.agenda.data.dto.toAgenda
import com.example.tasky.agenda.domain.model.Agenda
import com.example.tasky.agenda.domain.AgendaRepository
import com.example.tasky.core.domain.DataError
import com.example.tasky.db.TaskyDatabase
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.http.HttpMethod
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class AgendaRepositoryImpl(
    private val client: HttpClient,
    private val localEventDataSource: EventDataSource,
    private val localTaskDataSource: TaskDataSource,
    private val localReminderDataSource: ReminderDataSource,
    private val database: TaskyDatabase,
    private val applicationScope: CoroutineScope
) : AgendaRepository {

    private val agendaUrl = "${BuildConfig.BASE_URL}/agenda"

    override suspend fun getDailyAgenda(time: Long): Result<Agenda, DataError> {
        val result: Result<AgendaDTO, DataError> = client.executeRequest<Unit, AgendaDTO>(
            httpMethod = HttpMethod.Get,
            url = agendaUrl,
            queryParams = Pair(QUERY_PARAM_KEY_TIME, time),
            tag = TAG
        ) {
            Result.Success(it.body())
        }

        return when (result) {
            is Result.Success -> {
                val agendaDTO = result.data

                database.transaction {
                    applicationScope.launch {
                        applicationScope.launch {
                            localEventDataSource.insertOrReplaceEvents(agendaDTO.events)
                            localTaskDataSource.insertOrReplaceTasks(agendaDTO.tasks)
                            localReminderDataSource.insertOrReplaceReminders(agendaDTO.reminders)
                        }.join()
                    }
                }

                Result.Success(agendaDTO.toAgenda())
            }
            is Result.Error -> result
        }
    }

    companion object {
        private const val TAG = "AgendaRepository"

        private const val QUERY_PARAM_KEY_TIME = "time"
    }
}