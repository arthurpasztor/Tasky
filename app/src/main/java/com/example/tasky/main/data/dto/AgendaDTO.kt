package com.example.tasky.main.data.dto

import com.example.tasky.main.domain.Agenda
import com.example.tasky.main.domain.AgendaListItem
import kotlinx.serialization.Serializable

@Serializable
class AgendaDTO(
    val tasks: List<TaskDTO> = emptyList(),
    val reminders: List<ReminderDTO> = emptyList()
)