package com.example.tasky.agenda.data.db

import com.example.tasky.agenda.data.dto.EventDTO
import com.example.tasky.migrations.EventEntity
import kotlinx.coroutines.flow.Flow

interface EventDataSource {

    suspend fun getEventById(id: String): EventEntity?

    suspend fun getAllEvents(): Flow<List<EventEntity>>

    suspend fun insertOrReplaceEvents(events: List<EventDTO>)

    suspend fun insertOrReplaceEvent(event: EventDTO)

    suspend fun deleteEvent(id: String)
}