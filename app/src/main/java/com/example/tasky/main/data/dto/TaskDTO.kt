package com.example.tasky.main.data.dto

import com.example.tasky.main.domain.formatAgendaTimestamp
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class TaskDTO(
    val id: String,
    val title: String,
    val description: String,
    val time: Long,
    val remindAt: Long,
    val isDone: Boolean
) : AgendaListItem {

    override fun getTimestamp() = time

    override fun getItemTitle() = title

    override fun getItemDescription() = description

    override fun getFormattedTime() = time.formatAgendaTimestamp()

    override fun isItemDone() = isDone

    companion object {
        fun getSampleTask() = TaskDTO(
            id = UUID.randomUUID().toString(),
            title = "Sample Task",
            description = "This is a sample task",
            time = System.currentTimeMillis(),
            remindAt = System.currentTimeMillis() + 3600000,
            isDone = true
        )
    }
}