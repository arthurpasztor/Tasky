package com.example.tasky.main.data.dto

import kotlinx.serialization.Serializable

@Serializable
class AgendaDTO(
    val tasks: List<TaskDTO> = emptyList(),
    val reminders: List<ReminderDTO> = emptyList()
) {
    fun getAgendaItemsAsList() : List<AgendaListItem> {
        val items = mutableListOf<AgendaListItem>().apply {
            addAll(tasks)
            addAll(reminders)

            sortBy { it.getTimestamp() }
        }

        return items
    }

    companion object {

        fun getEmpty() = AgendaDTO()

        fun getSample() = AgendaDTO(
            tasks = listOf(TaskDTO.getSampleTask()),
            reminders = listOf(ReminderDTO.getSampleReminder())
        )
    }
}