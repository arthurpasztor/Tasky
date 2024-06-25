package com.example.tasky.agenda.data

import com.example.tasky.BuildConfig
import com.example.tasky.agenda.data.dto.EventDTO
import com.example.tasky.agenda.data.dto.NewAttendeeDTO
import com.example.tasky.agenda.data.dto.toAttendee
import com.example.tasky.agenda.data.dto.toEvent
import com.example.tasky.agenda.data.dto.toEventDTO
import com.example.tasky.agenda.domain.EventRepository
import com.example.tasky.agenda.domain.model.AgendaListItem.Event
import com.example.tasky.agenda.domain.model.NewAttendee
import com.example.tasky.core.data.executeMultipartRequest
import com.example.tasky.core.data.executeRequest
import com.example.tasky.core.domain.DataError
import com.example.tasky.core.domain.Result
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.http.HttpMethod

private const val CREATE_EVENT_MULTIPART_JSON_KEY = "create_event_request"

class EventRepositoryImpl(private val client: HttpClient) : EventRepository {

    private val eventUrl = "${BuildConfig.BASE_URL}/event"
    private val attendeeUrl = "${BuildConfig.BASE_URL}/attendee"

    override suspend fun getAttendee(email: String): Result<NewAttendee, DataError> {
        val result: Result<NewAttendeeDTO, DataError> = client.executeRequest<Unit, NewAttendeeDTO>(
            httpMethod = HttpMethod.Get,
            url = attendeeUrl,
            queryParams = Pair(QUERY_PARAM_KEY_EMAIL, email),
            tag = TAG
        ) {
            Result.Success(it.body())
        }

        return when (result) {
            is Result.Success -> Result.Success(result.data.toAttendee())
            is Result.Error -> result
        }
    }

    override suspend fun createEvent(
        event: Event,
        imageBytes: List<ByteArray>
    ): Result<Event, DataError> {
        val result: Result<EventDTO, DataError> = client.executeMultipartRequest<EventDTO, EventDTO>(
            httpMethod = HttpMethod.Post,
            url = eventUrl,
            key = CREATE_EVENT_MULTIPART_JSON_KEY,
            payload = event.toEventDTO(withPhotos = false),
            imageBytes = imageBytes,
            tag = TAG
        ) {
            Result.Success(it.body())
        }

        return when (result) {
            is Result.Success -> Result.Success(result.data.toEvent())
            is Result.Error -> result
        }
    }

    companion object {
        private const val TAG = "EventRepository"

        private const val QUERY_PARAM_KEY_EMAIL = "email"
    }
}