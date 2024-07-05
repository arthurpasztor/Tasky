package com.example.tasky.agenda.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class DeleteAgendaItemIdsDTO(
    val deletedEventIds: List<String>,
    val deletedTaskIds: List<String>,
    val deletedReminderIds: List<String>
) {
    fun isEmpty() = deletedEventIds.isEmpty() && deletedTaskIds.isEmpty() && deletedReminderIds.isEmpty()
}
