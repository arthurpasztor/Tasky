package com.example.tasky.agenda.data.db

import com.example.tasky.migrations.DeleteReminderIdEntity
import com.example.tasky.migrations.DeletedEventIdEntity
import com.example.tasky.migrations.DeletedTaskIdEntity

interface DeleteAgendaItemDataSource {

    suspend fun getAllEventIds(offlineUserAuthorId: String): List<DeletedEventIdEntity>

    suspend fun insertOrReplaceEventId(id: String, offlineUserAuthorId: String? = null)

    suspend fun getAllTaskIds(offlineUserAuthorId: String): List<DeletedTaskIdEntity>

    suspend fun insertOrReplaceTaskId(id: String, offlineUserAuthorId: String? = null)

    suspend fun getAllReminderIds(offlineUserAuthorId: String): List<DeleteReminderIdEntity>

    suspend fun insertOrReplaceReminderId(id: String, offlineUserAuthorId: String? = null)

    suspend fun clearAll(offlineUserAuthorId: String)
}