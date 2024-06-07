package com.example.tasky.agenda.domain

import com.example.tasky.core.domain.Result
import com.example.tasky.core.domain.RootError
import com.example.tasky.agenda.domain.AgendaListItem.Reminder

interface ReminderRepository {
    suspend fun createReminder(reminder: Reminder): Result<Unit, RootError>
    suspend fun updateReminder(reminder: Reminder): Result<Unit, RootError>
}