package com.example.tasky.agenda.domain

import com.example.tasky.agenda.domain.model.AgendaListItem.Reminder
import com.example.tasky.core.domain.DataError
import com.example.tasky.core.domain.Result

interface ReminderRepository {
    suspend fun createReminder(reminder: Reminder): Result<Unit, DataError>
    suspend fun updateReminder(reminder: Reminder): Result<Unit, DataError>
}