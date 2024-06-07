package com.example.tasky.auth.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class SignUpDTO(
    val fullName: String,
    val email: String,
    val password: String
)
