package com.example.tasky.agenda.data.db

interface DeleteAgendaItemDataSource {

    suspend fun getAllEventIds(): List<String>

    suspend fun insertOrReplaceEventId(id: String)

    suspend fun getAllTaskIds(): List<String>

    suspend fun insertOrReplaceTaskId(id: String)

    suspend fun getAllReminderIds(): List<String>

    suspend fun insertOrReplaceReminderId(id: String)

    suspend fun clearAll()
}