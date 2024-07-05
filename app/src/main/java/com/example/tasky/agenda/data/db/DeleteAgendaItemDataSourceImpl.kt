package com.example.tasky.agenda.data.db

import com.example.tasky.db.TaskyDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DeleteAgendaItemDataSourceImpl(db: TaskyDatabase) : DeleteAgendaItemDataSource {

    private val queries = db.deleteAgendaItemIdsQueries

    override suspend fun getAllEventIds(): List<String> {
        return withContext(Dispatchers.IO) {
            queries.getAllDeletedEventIds().executeAsList()
        }
    }

    override suspend fun insertOrReplaceEventId(id: String) {
        withContext(Dispatchers.IO) {
            queries.insertOrReplaceEventId(id)
        }
    }

    override suspend fun getAllTaskIds(): List<String> {
        return withContext(Dispatchers.IO) {
            queries.getAllDeletedTaskIds().executeAsList()
        }
    }

    override suspend fun insertOrReplaceTaskId(id: String) {
        withContext(Dispatchers.IO) {
            queries.insertOrReplaceTaskId(id)
        }
    }

    override suspend fun getAllReminderIds(): List<String> {
        return withContext(Dispatchers.IO) {
            queries.getAllDeletedReminderIds().executeAsList()
        }
    }

    override suspend fun insertOrReplaceReminderId(id: String) {
        withContext(Dispatchers.IO) {
            queries.insertOrReplaceReminderId(id)
        }
    }
}