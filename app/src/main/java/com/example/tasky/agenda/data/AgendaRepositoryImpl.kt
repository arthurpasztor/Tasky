package com.example.tasky.agenda.data

import com.example.tasky.BuildConfig
import com.example.tasky.agenda.data.db.AgendaDataSource
import com.example.tasky.core.data.executeRequest
import com.example.tasky.core.domain.Result
import com.example.tasky.agenda.data.dto.AgendaDTO
import com.example.tasky.agenda.data.dto.toAgenda
import com.example.tasky.agenda.domain.model.Agenda
import com.example.tasky.agenda.domain.AgendaRepository
import com.example.tasky.agenda.domain.NetworkConnectivityMonitor
import com.example.tasky.agenda.domain.getFormattedLocalDateFromMillis
import com.example.tasky.core.domain.DataError
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.http.HttpMethod
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class AgendaRepositoryImpl(
    private val client: HttpClient,
    private val localDataSource: AgendaDataSource,
    private val applicationScope: CoroutineScope,
    private val networkMonitor: NetworkConnectivityMonitor,
) : AgendaRepository {

    private val agendaUrl = "${BuildConfig.BASE_URL}/agenda"
    private val fullAgendaUrl = "${BuildConfig.BASE_URL}/fullAgenda"

    override suspend fun getDailyAgenda(time: Long): Result<Agenda, DataError> {
        return if (networkMonitor.isNetworkAvailable()) {
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

                    applicationScope.launch {
                        localDataSource.insertOrReplaceAgendaItems(agendaDTO)
                    }.join()

                    Result.Success(agendaDTO.toAgenda())
                }

                is Result.Error -> result
            }
        } else {
            val todayFormatted = time.getFormattedLocalDateFromMillis()
            val agendaItemsList = localDataSource.getAllAgendaItemsByDay(todayFormatted)

            Result.Success(Agenda(agendaItemsList))
        }
    }

    override suspend fun syncFullAgenda(): Result<Agenda, DataError> {
        return if (networkMonitor.isNetworkAvailable()) {
            val result: Result<AgendaDTO, DataError> = client.executeRequest<Unit, AgendaDTO>(
                httpMethod = HttpMethod.Get,
                url = fullAgendaUrl,
                tag = TAG
            ) {
                Result.Success(it.body())
            }
            when (result) {
                is Result.Success -> {
                    val agendaDTO = result.data

                    applicationScope.launch {
                        localDataSource.insertOrReplaceAgendaItems(agendaDTO)
                    }.join()

                    Result.Success(agendaDTO.toAgenda())
                }

                is Result.Error -> result
            }
        } else {
            Result.Error(DataError.LocalError.NO_INTERNET_CONNECTION)
        }
    }

    companion object {
        private const val TAG = "AgendaRepository"

        private const val QUERY_PARAM_KEY_TIME = "time"
    }
}