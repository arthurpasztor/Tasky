package com.example.tasky.main.data.dto

import com.example.tasky.main.domain.Agenda
import com.example.tasky.main.domain.AgendaListItem
import kotlinx.serialization.Serializable

@Serializable
class AgendaDTO(
    val tasks: List<TaskDTO> = emptyList(),
    val reminders: List<ReminderDTO> = emptyList()
) {
    fun toAgenda(): Agenda {
        val items = mutableListOf<AgendaListItem>().apply {
            addAll(tasks.map { it.toTask() })
            addAll(reminders.map { it.toReminder() })

            sortBy { it.time }
        }

        return Agenda(items)
    }
}