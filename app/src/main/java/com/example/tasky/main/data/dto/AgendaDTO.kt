package com.example.tasky.main.data.dto

import kotlinx.serialization.Serializable

@Serializable
class AgendaDTO(
    val tasks: List<TaskDTO> = emptyList(),
    val reminders: List<ReminderDTO> = emptyList()
)