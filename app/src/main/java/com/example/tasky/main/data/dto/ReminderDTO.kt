package com.example.tasky.main.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class ReminderDTO(
    val id: String,
    val title: String,
    val description: String,
    val time: Long,
    val remindAt: Long
)