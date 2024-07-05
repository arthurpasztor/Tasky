package com.example.tasky.agenda.data.db

import com.example.tasky.agenda.data.dto.ReminderDTO
import com.example.tasky.agenda.domain.model.OfflineStatus
import com.example.tasky.migrations.ReminderEntity
import kotlinx.coroutines.flow.Flow

interface ReminderDataSource {

    suspend fun getReminderById(id: String): ReminderEntity?

    suspend fun getAllReminders(): Flow<List<ReminderEntity>>

    suspend fun insertOrReplaceReminder(reminder: ReminderDTO, offlineStatus: OfflineStatus? = null)

    suspend fun deleteReminder(id: String)

    suspend fun deleteAllReminders()
}