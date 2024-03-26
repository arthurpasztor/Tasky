package com.example.tasky.auth.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class SignUpRequest(
    val fullName: String,
    val email: String,
    val password: String
)
