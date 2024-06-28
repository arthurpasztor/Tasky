package com.example.tasky.agenda.domain

import com.example.tasky.agenda.domain.model.AgendaListItem.Reminder
import com.example.tasky.core.domain.DataError
import com.example.tasky.core.domain.EmptyResult
import com.example.tasky.core.domain.Result

interface ReminderRepository {
    suspend fun createReminder(reminder: Reminder): EmptyResult<DataError>
    suspend fun updateReminder(reminder: Reminder): EmptyResult<DataError>
    suspend fun deleteReminder(reminderId: String): EmptyResult<DataError>
    suspend fun getReminderDetails(reminderId: String): Result<Reminder, DataError>
}