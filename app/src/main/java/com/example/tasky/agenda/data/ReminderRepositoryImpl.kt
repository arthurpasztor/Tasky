package com.example.tasky.agenda.data

import com.example.tasky.BuildConfig
import com.example.tasky.agenda.data.db.ReminderDataSource
import com.example.tasky.agenda.data.dto.ReminderDTO
import com.example.tasky.agenda.data.dto.TaskDTO
import com.example.tasky.agenda.data.dto.toReminder
import com.example.tasky.agenda.data.dto.toReminderDTO
import com.example.tasky.agenda.data.dto.toTask
import com.example.tasky.agenda.domain.ReminderRepository
import com.example.tasky.agenda.domain.model.AgendaListItem
import com.example.tasky.core.data.executeRequest
import com.example.tasky.core.domain.DataError
import com.example.tasky.core.domain.EmptyResult
import com.example.tasky.core.domain.Result
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.http.HttpMethod

class ReminderRepositoryImpl(
    private val client: HttpClient,
    private val localDataSource: ReminderDataSource
) : ReminderRepository {

    private val reminderUrl = "${BuildConfig.BASE_URL}/reminder"

    override suspend fun createReminder(reminder: AgendaListItem.Reminder): EmptyResult<DataError> {
        val reminderDTO = reminder.toReminderDTO()

        return client.executeRequest<ReminderDTO, Unit>(
            httpMethod = HttpMethod.Post,
            url = reminderUrl,
            payload = reminderDTO,
            tag = TAG
        ) {
            localDataSource.insertOrReplaceReminder(reminderDTO)
            Result.Success(Unit)
        }
    }

    override suspend fun updateReminder(reminder: AgendaListItem.Reminder): EmptyResult<DataError> {
        val reminderDTO = reminder.toReminderDTO()

        return client.executeRequest<ReminderDTO, Unit>(
            httpMethod = HttpMethod.Put,
            url = reminderUrl,
            payload = reminderDTO,
            tag = TAG
        ) {
            localDataSource.insertOrReplaceReminder(reminderDTO)
            Result.Success(Unit)
        }
    }

    override suspend fun deleteReminder(reminderId: String): EmptyResult<DataError> {
        return client.executeRequest<Unit, Unit>(
            httpMethod = HttpMethod.Delete,
            url = reminderUrl,
            queryParams = Pair(QUERY_PARAM_KEY_ID, reminderId),
            tag = TAG
        ) {
            localDataSource.deleteReminder(reminderId)
            Result.Success(Unit)
        }
    }

    override suspend fun getReminderDetails(reminderId: String): Result<AgendaListItem.Reminder, DataError> {
        val result = client.executeRequest<Unit, ReminderDTO>(
            httpMethod = HttpMethod.Get,
            url = reminderUrl,
            queryParams = Pair(QUERY_PARAM_KEY_ID, reminderId),
            tag = TAG
        ) {
            Result.Success(it.body())
        }

        return when (result) {
            is Result.Success -> {
                localDataSource.insertOrReplaceReminder(result.data)
                Result.Success(result.data.toReminder())
            }

            is Result.Error -> Result.Error(result.error)
        }
    }

    companion object {
        private const val TAG = "ReminderRepository"

        private const val QUERY_PARAM_KEY_ID = "reminderId"
    }
}