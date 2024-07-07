package com.example.tasky.agenda.data.db

import com.example.tasky.agenda.data.dto.EventDTO
import com.example.tasky.agenda.domain.model.OfflineStatus
import com.example.tasky.migrations.EventEntity

interface EventDataSource {

    suspend fun getEventById(id: String): EventEntity?

    suspend fun getAllOfflineEvents(offlineUserAuthorId: String, offlineStatus: OfflineStatus): List<EventEntity>

    suspend fun getAllEvents(): List<EventEntity>

    suspend fun insertOrReplaceEvent(
        event: EventDTO,
        deletedPhotoKeys: List<String> = emptyList(),
        offlineUserAuthorId: String? = null,
        offlineStatus: OfflineStatus? = null
    )

    suspend fun deleteEvent(id: String)

    suspend fun deleteAllEvents()
}