package com.example.tasky.agenda.data.dto

import kotlinx.serialization.Serializable

@Serializable
class AgendaDTO(
    val events: List<EventDTO> = emptyList(),
    val tasks: List<TaskDTO> = emptyList(),
    val reminders: List<ReminderDTO> = emptyList()
)