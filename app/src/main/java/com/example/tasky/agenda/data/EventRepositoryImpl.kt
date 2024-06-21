package com.example.tasky.agenda.data

import com.example.tasky.BuildConfig
import com.example.tasky.agenda.data.dto.NewAttendeeDTO
import com.example.tasky.agenda.data.dto.toAttendee
import com.example.tasky.agenda.domain.EventRepository
import com.example.tasky.agenda.domain.model.NewAttendee
import com.example.tasky.core.data.executeRequest
import com.example.tasky.core.domain.DataError
import com.example.tasky.core.domain.Result
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.http.HttpMethod

class EventRepositoryImpl(private val client: HttpClient) : EventRepository {

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

    companion object {
        private const val TAG = "EventRepository"

        private const val QUERY_PARAM_KEY_EMAIL = "email"
    }
}