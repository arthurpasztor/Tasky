package com.example.tasky.main.presentation

import com.example.tasky.main.domain.AgendaListItem.ReminderDM
import com.example.tasky.main.domain.AgendaListItem.TaskDM

sealed interface AgendaItemUi {
    fun getTitle(): String = ""
    fun getDescription(): String = ""
    fun getFormattedTime(): String = ""
    fun isDone(): Boolean = false

    data class TaskUi(val task: TaskDM): AgendaItemUi {
        override fun getTitle(): String = task.title
        override fun getDescription() = task.description
        override fun getFormattedTime() = task.getFormattedTime()
        override fun isDone() = task.isDone
    }

    data class ReminderUi(val reminder: ReminderDM): AgendaItemUi {
        override fun getTitle(): String = reminder.title
        override fun getDescription() = reminder.description
        override fun getFormattedTime() = reminder.getFormattedTime()

    }

    data object NeedleUi: AgendaItemUi
}
