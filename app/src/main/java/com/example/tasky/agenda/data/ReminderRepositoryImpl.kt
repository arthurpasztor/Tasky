package com.example.tasky.agenda.data

import com.example.tasky.BuildConfig
import com.example.tasky.core.data.executeRequest
import com.example.tasky.core.domain.Result
import com.example.tasky.core.domain.RootError
import com.example.tasky.agenda.data.dto.ReminderDTO
import com.example.tasky.agenda.data.dto.toReminderDTO
import com.example.tasky.agenda.domain.AgendaListItem
import com.example.tasky.agenda.domain.ReminderRepository
import io.ktor.client.HttpClient
import io.ktor.http.HttpMethod

class ReminderRepositoryImpl(private val client: HttpClient) : ReminderRepository {

    private val reminderUrl = "${BuildConfig.BASE_URL}/reminder"
    override suspend fun createReminder(reminder: AgendaListItem.ReminderDM): Result<Unit, RootError> {
        return client.executeRequest<ReminderDTO, Unit>(
            httpMethod = HttpMethod.Post,
            url = reminderUrl,
            payload = reminder.toReminderDTO(),
            tag = TAG
        ) {
            Result.Success(Unit)
        }
    }

    override suspend fun updateReminder(reminder: AgendaListItem.ReminderDM): Result<Unit, RootError> {
        TODO("Not yet implemented")
    }

    companion object {
        private const val TAG = "ReminderRepository"
    }
}