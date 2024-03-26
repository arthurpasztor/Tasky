package com.example.tasky.auth.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class TokenResponse(
    val token: String,
    val userId: String,
    val fullName: String
)
