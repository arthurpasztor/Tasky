package com.example.tasky.agenda.data.db

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.example.tasky.agenda.data.dto.EventDTO
import com.example.tasky.agenda.domain.getFormattedLocalDateFromMillis
import com.example.tasky.agenda.domain.model.OfflineStatus
import com.example.tasky.db.TaskyDatabase
import com.example.tasky.migrations.EventEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class EventDataSourceImpl(db: TaskyDatabase) : EventDataSource {

    private val queries = db.eventEntityQueries

    override suspend fun getEventById(id: String): EventEntity? {
        return withContext(Dispatchers.IO) {
            queries.getEventById(id).executeAsOneOrNull()
        }
    }

    override suspend fun getAllEvents(): Flow<List<EventEntity>> {
        return withContext(Dispatchers.IO) {
            queries.getAllEvents().asFlow().mapToList(this.coroutineContext)
        }
    }

    override suspend fun insertOrReplaceEvent(
        event: EventDTO,
        deletedPhotoKeys: List<String>,
        offlineStatus: OfflineStatus?
    ) {
        withContext(Dispatchers.IO) {
            queries.insertOrReplaceEvent(
                event.id,
                event.title,
                event.description,
                event.from,
                event.to,
                event.remindAt,
                event.host,
                event.isUserEventCreator,
                event.attendees,
                event.photos,
                event.from.getFormattedLocalDateFromMillis(),
                deletedPhotoKeys,
                offlineStatus
            )
        }
    }

    override suspend fun deleteEvent(id: String) {
        withContext(Dispatchers.IO) {
            queries.deleteEventById(id)
        }
    }

    override suspend fun deleteAllEvents() {
        withContext(Dispatchers.IO) {
            queries.deleteAll()
        }
    }
}