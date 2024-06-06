package com.example.tasky.main.data.dto

import com.example.tasky.main.domain.AgendaListItem.Task
import com.example.tasky.main.domain.getLocalDateTimeFromMillis
import com.example.tasky.main.domain.getMillis
import kotlinx.serialization.Serializable

@Serializable
data class TaskDTO(
    val id: String,
    val title: String,
    val description: String,
    val time: Long,
    val remindAt: Long,
    val isDone: Boolean
)