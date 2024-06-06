package com.example.tasky.main.data.dto

import com.example.tasky.main.domain.AgendaListItem
import com.example.tasky.main.domain.getLocalDateTimeFromMillis
import kotlinx.serialization.Serializable

@Serializable
data class TaskDTO(
    val id: String,
    val title: String,
    val description: String,
    val time: Long,
    val remindAt: Long,
    val isDone: Boolean
) {

    fun toTask() = AgendaListItem.Task(
        id = id,
        title = title,
        description = description,
        time = time.getLocalDateTimeFromMillis(),
        remindAt = remindAt.getLocalDateTimeFromMillis(),
        isDone = isDone
    )
}