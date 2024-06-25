package com.example.tasky.agenda.domain

import com.example.tasky.agenda.domain.model.AgendaListItem.Event
import com.example.tasky.agenda.domain.model.NewAttendee
import com.example.tasky.core.domain.DataError
import com.example.tasky.core.domain.Result

interface EventRepository {
    suspend fun createEvent(event: Event, imageBytes: List<Pair<String, ByteArray>>): Result<Event, DataError>

    suspend fun getAttendee(email: String): Result<NewAttendee, DataError>
}