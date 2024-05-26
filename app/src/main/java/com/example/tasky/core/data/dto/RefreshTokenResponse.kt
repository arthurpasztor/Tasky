package com.example.tasky.core.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class RefreshTokenResponse(
    val accessToken: String,
    val expirationTimestamp: Long
)
