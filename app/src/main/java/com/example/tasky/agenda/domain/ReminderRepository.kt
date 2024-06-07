package com.example.tasky.agenda.domain

import com.example.tasky.core.domain.Result
import com.example.tasky.core.domain.RootError
import com.example.tasky.agenda.domain.AgendaListItem.ReminderDM

interface ReminderRepository {
    suspend fun createReminder(reminder: ReminderDM): Result<Unit, RootError>
    suspend fun updateReminder(reminder: ReminderDM): Result<Unit, RootError>
}