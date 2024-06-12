package com.example.tasky.agenda.data

import com.example.tasky.BuildConfig
import com.example.tasky.agenda.data.dto.ReminderDTO
import com.example.tasky.agenda.data.dto.toReminderDTO
import com.example.tasky.agenda.domain.ReminderRepository
import com.example.tasky.agenda.domain.model.AgendaListItem
import com.example.tasky.core.data.executeRequest
import com.example.tasky.core.domain.DataError
import com.example.tasky.core.domain.Result
import io.ktor.client.HttpClient
import io.ktor.http.HttpMethod

class ReminderRepositoryImpl(private val client: HttpClient) : ReminderRepository {

    private val reminderUrl = "${BuildConfig.BASE_URL}/reminder"

    override suspend fun createReminder(reminder: AgendaListItem.Reminder): Result<Unit, DataError> {
        return client.executeRequest<ReminderDTO, Unit>(
            httpMethod = HttpMethod.Post,
            url = reminderUrl,
            payload = reminder.toReminderDTO(),
            tag = TAG
        ) {
            Result.Success(Unit)
        }
    }

    override suspend fun updateReminder(reminder: AgendaListItem.Reminder): Result<Unit, DataError> {
        return client.executeRequest<ReminderDTO, Unit>(
            httpMethod = HttpMethod.Put,
            url = reminderUrl,
            payload = reminder.toReminderDTO(),
            tag = TAG
        ) {
            Result.Success(Unit)
        }
    }

    override suspend fun deleteReminder(reminderId: String): Result<Unit, DataError> {
        return client.executeRequest<Unit, Unit>(
            httpMethod = HttpMethod.Delete,
            url = reminderUrl,
            queryParams = Pair(QUERY_PARAM_KEY_ID, reminderId),
            tag = TAG
        ) {
            Result.Success(Unit)
        }
    }

    companion object {
        private const val TAG = "ReminderRepository"

        private const val QUERY_PARAM_KEY_ID = "reminderId"
    }
}