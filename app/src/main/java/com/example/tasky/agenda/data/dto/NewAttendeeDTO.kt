package com.example.tasky.agenda.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class NewAttendeeDTO(
    val doesUserExist: Boolean,
    val attendee: NewAttendeeInfoDTO?
)

@Serializable
data class NewAttendeeInfoDTO(
    val email: String,
    val fullName: String,
    val userId: String,
)