package com.example.tasky.agenda.domain.model

data class NewAttendee(
    val doesUserExist: Boolean,
    val email: String?,
    val fullName: String?,
    val userId: String?,
)
