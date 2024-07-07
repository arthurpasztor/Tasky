package com.example.tasky.agenda.data.db

import com.example.tasky.db.TaskyDatabase
import com.example.tasky.migrations.DeleteReminderIdEntity
import com.example.tasky.migrations.DeletedEventIdEntity
import com.example.tasky.migrations.DeletedTaskIdEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DeleteAgendaItemDataSourceImpl(db: TaskyDatabase) : DeleteAgendaItemDataSource {

    private val queries = db.deleteAgendaItemIdsQueries

    override suspend fun getAllEventIds(offlineUserAuthorId: String): List<DeletedEventIdEntity> {
        return withContext(Dispatchers.IO) {
            queries.getAllDeletedEventIds(offlineUserAuthorId).executeAsList()
        }
    }

    override suspend fun insertOrReplaceEventId(id: String, offlineUserAuthorId: String?) {
        withContext(Dispatchers.IO) {
            queries.insertOrReplaceEventId(id, offlineUserAuthorId)
        }
    }

    override suspend fun getAllTaskIds(offlineUserAuthorId: String): List<DeletedTaskIdEntity> {
        return withContext(Dispatchers.IO) {
            queries.getAllDeletedTaskIds(offlineUserAuthorId).executeAsList()
        }
    }

    override suspend fun insertOrReplaceTaskId(id: String, offlineUserAuthorId: String?) {
        withContext(Dispatchers.IO) {
            queries.insertOrReplaceTaskId(id, offlineUserAuthorId)
        }
    }

    override suspend fun getAllReminderIds(offlineUserAuthorId: String): List<DeleteReminderIdEntity> {
        return withContext(Dispatchers.IO) {
            queries.getAllDeletedReminderIds(offlineUserAuthorId).executeAsList()
        }
    }

    override suspend fun insertOrReplaceReminderId(id: String, offlineUserAuthorId: String?) {
        withContext(Dispatchers.IO) {
            queries.insertOrReplaceReminderId(id, offlineUserAuthorId)
        }
    }

    override suspend fun clearAll(offlineUserAuthorId: String) {
        withContext(Dispatchers.IO) {
            queries.deleteAllEventIds(offlineUserAuthorId)
            queries.deleteAllTaskIds(offlineUserAuthorId)
            queries.deleteAllReminderIds(offlineUserAuthorId)
        }
    }
}