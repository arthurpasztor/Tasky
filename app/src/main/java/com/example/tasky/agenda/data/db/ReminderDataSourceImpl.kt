package com.example.tasky.agenda.data.db

import com.example.tasky.agenda.data.dto.ReminderDTO
import com.example.tasky.agenda.domain.getFormattedLocalDateFromMillis
import com.example.tasky.agenda.domain.model.OfflineStatus
import com.example.tasky.migrations.ReminderEntity
import com.example.tasky.db.TaskyDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ReminderDataSourceImpl(db: TaskyDatabase) : ReminderDataSource {

    private val queries = db.reminderEntityQueries

    override suspend fun getReminderById(id: String): ReminderEntity? {
        return withContext(Dispatchers.IO) {
            queries.getReminderById(id).executeAsOneOrNull()
        }
    }

    override suspend fun getAllOfflineReminders(offlineStatus: OfflineStatus): List<ReminderEntity> {
        return withContext(Dispatchers.IO) {
            queries.getAllOfflineReminders(offlineStatus).executeAsList()
        }
    }

    override suspend fun getAllReminders(): List<ReminderEntity> {
        return withContext(Dispatchers.IO) {
            queries.getAllReminders().executeAsList()
        }
    }

    override suspend fun insertOrReplaceReminder(reminder: ReminderDTO, offlineStatus: OfflineStatus?) {
        withContext(Dispatchers.IO) {
            queries.insertOrReplaceReminder(
                reminder.id,
                reminder.title,
                reminder.description,
                reminder.time,
                reminder.remindAt,
                reminder.time.getFormattedLocalDateFromMillis(),
                offlineStatus
            )
        }
    }

    override suspend fun deleteReminder(id: String) {
        withContext(Dispatchers.IO) {
            queries.deleteReminderById(id)
        }
    }

    override suspend fun deleteAllReminders() {
        withContext(Dispatchers.IO) {
            queries.deleteAll()
        }
    }
}