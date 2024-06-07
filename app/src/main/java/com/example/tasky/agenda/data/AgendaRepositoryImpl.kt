package com.example.tasky.agenda.data

import com.example.tasky.BuildConfig
import com.example.tasky.core.data.executeRequest
import com.example.tasky.core.domain.Result
import com.example.tasky.core.domain.RootError
import com.example.tasky.agenda.data.dto.AgendaDTO
import com.example.tasky.agenda.data.dto.toAgenda
import com.example.tasky.agenda.domain.Agenda
import com.example.tasky.agenda.domain.AgendaRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.http.HttpMethod

class AgendaRepositoryImpl(private val client: HttpClient) : AgendaRepository {

    private val agendaUrl = "${BuildConfig.BASE_URL}/agenda"

    override suspend fun getDailyAgenda(time: Long): Result<Agenda, RootError> {
        val result: Result<AgendaDTO, RootError> = client.executeRequest<Unit, AgendaDTO>(
            httpMethod = HttpMethod.Get,
            url = agendaUrl,
            queryParams = Pair(QUERY_PARAM_KEY_TIME, time),
            tag = TAG
        ) {
            Result.Success(it.body())
        }

        return when (result) {
            is Result.Success ->  Result.Success(result.data.toAgenda())
            is Result.Error -> result
        }
    }

    companion object {
        private const val TAG = "AgendaRepository"

        private const val QUERY_PARAM_KEY_TIME = "time"
    }
}