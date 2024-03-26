package com.example.tasky.auth.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class AuthError(
    val message: String
)
