package com.example.tasky.auth.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class TokenDTO(
    val accessToken: String,
    val refreshToken: String,
    val userId: String,
    val fullName: String,
    val accessTokenExpirationTimestamp: Long
)
