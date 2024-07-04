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
import com.example.tasky.agenda.data.dto.toEventDTO
import com.example.tasky.agenda.data.dto.toEventUpdateDTO
import com.example.tasky.agenda.domain.EventRepository
import com.example.tasky.agenda.domain.NetworkConnectivityMonitor
import com.example.tasky.agenda.domain.model.AgendaListItem.Event
import com.example.tasky.agenda.domain.model.EventUpdate
import com.example.tasky.agenda.domain.model.NewAttendee
import com.example.tasky.agenda.domain.model.OfflineStatus
import com.example.tasky.core.data.executeMultipartRequest
import com.example.tasky.core.data.executeRequest
import com.example.tasky.core.domain.DataError
import com.example.tasky.core.domain.EmptyResult
import com.example.tasky.core.domain.Result
import com.example.tasky.migrations.EventEntity
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
    private val applicationScope: CoroutineScope,
    private val networkMonitor: NetworkConnectivityMonitor
) : EventRepository {

    private val eventUrl = "${BuildConfig.BASE_URL}/event"
    private val attendeeUrl = "${BuildConfig.BASE_URL}/attendee"

    override suspend fun createEvent(event: Event, imageBytes: List<ByteArray>): Result<Event, DataError> {
        return if (networkMonitor.isNetworkAvailable()) {
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

            when (result) {
                is Result.Success -> {
                    applicationScope.launch {
                        localDataSource.insertOrReplaceEvent(result.data)
                    }.join()
                    Result.Success(result.data.toEvent())
                }

                is Result.Error -> result
            }
        } else {
            applicationScope.launch {
                localDataSource.insertOrReplaceEvent(event = event.toEventDTO(), offlineStatus = OfflineStatus.CREATED)
            }.join()

            Result.Success(event)
        }
    }

    override suspend fun updateEvent(eventUpdate: EventUpdate, imageBytes: List<ByteArray>): Result<Event, DataError> {
        return if (networkMonitor.isNetworkAvailable()) {
            val result: Result<EventDTO, DataError> = client.executeMultipartRequest<EventUpdateDTO, EventDTO>(
                httpMethod = HttpMethod.Put,
                url = eventUrl,
                key = UPDATE_EVENT_MULTIPART_JSON_KEY,
                payload = eventUpdate.toEventUpdateDTO(),
                imageBytes = imageBytes,
                tag = TAG
            ) {
                Result.Success(it.body())
            }

            when (result) {
                is Result.Success -> {
                    applicationScope.launch {
                        localDataSource.insertOrReplaceEvent(result.data)
                    }.join()
                    Result.Success(result.data.toEvent())
                }

                is Result.Error -> result
            }
        } else {
            var eventDTO: EventDTO? = null
            applicationScope.launch {
                localDataSource.getEventById(eventUpdate.id)?.let { eventEntity ->
                    val appendedOfflineStatus = if (eventEntity.isOfflineCreated()) {
                        OfflineStatus.CREATED
                    } else {
                        OfflineStatus.UPDATED
                    }

                    // create EventDTO with missing info from the DB
                    eventDTO = eventUpdate.toEventDTO(
                        eventEntity.host,
                        eventEntity.isUserEventCreator,
                        eventEntity.photos
                    )

                    // append the newly deleted photo IDs to the already existing ones
                    val combinedDeletedPhotoKeys = mutableListOf<String>().apply {
                        addAll(eventEntity.deletedPhotoKeys)
                        addAll(eventUpdate.deletedPhotoKeys)
                    }

                    localDataSource.insertOrReplaceEvent(
                        event = eventDTO!!,
                        deletedPhotoKeys = combinedDeletedPhotoKeys,
                        offlineStatus = appendedOfflineStatus
                    )
                } ?: run {
                    Result.Error(DataError.LocalError.NOT_FOUND)
                }
            }.join()

            // Non-null assertion permitted, since the null case is handled by returning an error
            Result.Success(eventDTO!!.toEvent())
        }
    }

    override suspend fun deleteEvent(eventId: String): EmptyResult<DataError> {
        //TODO handle offline use case
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
        return if (networkMonitor.isNetworkAvailable()) {
            val result = client.executeRequest<Unit, EventDTO>(
                httpMethod = HttpMethod.Get,
                url = eventUrl,
                queryParams = Pair(QUERY_PARAM_KEY_ID, eventId),
                tag = TAG
            ) {
                Result.Success(it.body())
            }

            when (result) {
                is Result.Success -> {
                    applicationScope.launch {
                        localDataSource.insertOrReplaceEvent(result.data)
                    }.join()
                    Result.Success(result.data.toEvent())
                }

                is Result.Error -> result
            }
        } else {
            val eventEntity = localDataSource.getEventById(eventId)
            if (eventEntity != null) {
                Result.Success(eventEntity.toEvent())
            } else {
                Result.Error(DataError.LocalError.NOT_FOUND)
            }
        }
    }

    override suspend fun getAttendee(email: String): Result<NewAttendee, DataError> {
        return if (networkMonitor.isNetworkAvailable()) {
            val result: Result<NewAttendeeDTO, DataError> = client.executeRequest<Unit, NewAttendeeDTO>(
                httpMethod = HttpMethod.Get,
                url = attendeeUrl,
                queryParams = Pair(QUERY_PARAM_KEY_EMAIL, email),
                tag = TAG
            ) {
                Result.Success(it.body())
            }

            when (result) {
                is Result.Success -> Result.Success(result.data.toAttendee())
                is Result.Error -> result
            }
        } else {
            Result.Error(DataError.LocalError.CANNOT_FETCH_USERS)
        }
    }

    private fun EventEntity?.isOfflineCreated() = this?.offlineStatus == OfflineStatus.CREATED

    companion object {
        private const val TAG = "EventRepository"

        private const val QUERY_PARAM_KEY_EMAIL = "email"
        private const val QUERY_PARAM_KEY_ID = "eventId"
    }
}