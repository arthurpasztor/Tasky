package com.example.tasky.agenda.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class EventDTO(
    val id: String,
    val title: String,
    val description: String,
    val from: Long,
    val to: Long,
    val remindAt: Long,
    val host: String,
    val isUserEventCreator: Boolean,
    val attendees: List<AttendeeDTO>,
    val photos: List<PhotoDTO>
)

@Serializable
data class AttendeeDTO(
    val email: String,
    val fullName: String,
    val userId: String,
    val eventId: String,
    val isGoing: Boolean,
    val remindAt: Long
)

@Serializable
data class PhotoDTO(
    val key: String,
    val url: String
)