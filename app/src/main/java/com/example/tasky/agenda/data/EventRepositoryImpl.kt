package com.example.tasky.agenda.data

import com.example.tasky.BuildConfig
import com.example.tasky.agenda.data.db.EventDataSource
import com.example.tasky.agenda.data.dto.EventCreateDTO
import com.example.tasky.agenda.data.dto.EventDTO
import com.example.tasky.agenda.data.dto.EventUpdateDTO
import com.example.tasky.agenda.data.dto.NewAttendeeDTO
import com.example.tasky.agenda.data.dto.toAttendee
import com.example.tasky.agenda.data.dto.toEvent
import com.example.tasky.agenda.data.dto.toEventCreateDTO
import com.example.tasky.agenda.data.dto.toEventUpdateDTO
import com.example.tasky.agenda.domain.EventRepository
import com.example.tasky.agenda.domain.model.AgendaListItem.Event
import com.example.tasky.agenda.domain.model.EventUpdate
import com.example.tasky.agenda.domain.model.NewAttendee
import com.example.tasky.core.data.executeMultipartRequest
import com.example.tasky.core.data.executeRequest
import com.example.tasky.core.domain.DataError
import com.example.tasky.core.domain.EmptyResult
import com.example.tasky.core.domain.Result
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.http.HttpMethod
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

private const val CREATE_EVENT_MULTIPART_JSON_KEY = "create_event_request"
private const val UPDATE_EVENT_MULTIPART_JSON_KEY = "update_event_request"

class EventRepositoryImpl(
    private val client: HttpClient,
    private val localDataSource: EventDataSource,
    private val applicationScope: CoroutineScope
) : EventRepository {

    private val eventUrl = "${BuildConfig.BASE_URL}/event"
    private val attendeeUrl = "${BuildConfig.BASE_URL}/attendee"

    override suspend fun createEvent(event: Event, imageBytes: List<ByteArray>): Result<Event, DataError> {
        val result: Result<EventDTO, DataError> = client.executeMultipartRequest<EventCreateDTO, EventDTO>(
            httpMethod = HttpMethod.Post,
            url = eventUrl,
            key = CREATE_EVENT_MULTIPART_JSON_KEY,
            payload = event.toEventCreateDTO(),
            imageBytes = imageBytes,
            tag = TAG
        ) {
            Result.Success(it.body())
        }

        return when (result) {
            is Result.Success -> {
                applicationScope.launch {
                    localDataSource.insertOrReplaceEvent(result.data)
                }.join()
                Result.Success(result.data.toEvent())
            }
            is Result.Error -> result
        }
    }

    override suspend fun updateEvent(event: EventUpdate, imageBytes: List<ByteArray>): Result<Event, DataError> {
        val result: Result<EventDTO, DataError> = client.executeMultipartRequest<EventUpdateDTO, EventDTO>(
            httpMethod = HttpMethod.Put,
            url = eventUrl,
            key = UPDATE_EVENT_MULTIPART_JSON_KEY,
            payload = event.toEventUpdateDTO(),
            imageBytes = imageBytes,
            tag = TAG
        ) {
            Result.Success(it.body())
        }

        return when (result) {
            is Result.Success -> {
                applicationScope.launch {
                    localDataSource.insertOrReplaceEvent(result.data)
                }.join()
                Result.Success(result.data.toEvent())
            }
            is Result.Error -> result
        }
    }

    override suspend fun deleteEvent(eventId: String): EmptyResult<DataError> {
        return client.executeRequest<Unit, Unit>(
            httpMethod = HttpMethod.Delete,
            url = eventUrl,
            queryParams = Pair(QUERY_PARAM_KEY_ID, eventId),
            tag = TAG
        ) {
            applicationScope.launch {
                localDataSource.deleteEvent(eventId)
            }.join()
            Result.Success(Unit)
        }
    }

    override suspend fun getEventDetails(eventId: String): Result<Event, DataError> {
        val result = client.executeRequest<Unit, EventDTO>(
            httpMethod = HttpMethod.Get,
            url = eventUrl,
            queryParams = Pair(QUERY_PARAM_KEY_ID, eventId),
            tag = TAG
        ) {
            Result.Success(it.body())
        }

        return when (result) {
            is Result.Success -> {
                applicationScope.launch {
                    localDataSource.insertOrReplaceEvent(result.data)
                }.join()
                Result.Success(result.data.toEvent())
            }
            is Result.Error -> result
        }
    }

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

    companion object {
        private const val TAG = "EventRepository"

        private const val QUERY_PARAM_KEY_EMAIL = "email"
        private const val QUERY_PARAM_KEY_ID = "eventId"
    }
}