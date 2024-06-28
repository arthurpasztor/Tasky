package com.example.tasky.agenda.data.db

import com.example.tasky.agenda.data.dto.ReminderDTO
import com.example.tasky.migrations.ReminderEntity
import kotlinx.coroutines.flow.Flow

interface ReminderDataSource {

    suspend fun getReminderById(id: String): ReminderEntity?

    suspend fun getAllReminders(): Flow<List<ReminderEntity>>

    suspend fun insertOrReplaceReminders(reminders: List<ReminderDTO>)

    suspend fun insertOrReplaceReminder(reminder: ReminderDTO)

    suspend fun deleteReminder(id: String)
}