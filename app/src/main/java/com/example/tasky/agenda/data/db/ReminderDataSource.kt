package com.example.tasky.agenda.data.db

import com.example.tasky.agenda.data.dto.ReminderDTO
import com.example.tasky.agenda.domain.model.OfflineStatus
import com.example.tasky.migrations.ReminderEntity

interface ReminderDataSource {

    suspend fun getReminderById(id: String): ReminderEntity?

    suspend fun getAllOfflineReminders(offlineUserAuthorId: String, offlineStatus: OfflineStatus): List<ReminderEntity>

    suspend fun getAllReminders(): List<ReminderEntity>

    suspend fun insertOrReplaceReminder(
        reminder: ReminderDTO,
        offlineUserAuthorId: String? = null,
        offlineStatus: OfflineStatus? = null
    )

    suspend fun deleteReminder(id: String)

    suspend fun deleteAllReminders()
}