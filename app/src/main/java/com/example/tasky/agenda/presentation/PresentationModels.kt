package com.example.tasky.agenda.presentation

import com.example.tasky.agenda.domain.AgendaItemType
import com.example.tasky.agenda.domain.model.AgendaListItem.Reminder
import com.example.tasky.agenda.domain.model.AgendaListItem.Task

sealed class AgendaItemUi {
    // Initializers needed because some classes (ex. NeedleUi) don't override the fields
    open val id: String = ""
    open val title: String = ""
    open val description: String = ""
    open val isDone: Boolean = false

    open fun getFormattedTime(): String = ""

    open fun getAgendaItemType(): AgendaItemType = AgendaItemType.UNKNOWN

    data class TaskUi(val task: Task): AgendaItemUi() {
        override val id = task.id
        override val title = task.title
        override val description = task.description
        override val isDone = task.isDone

        override fun getFormattedTime() = task.getFormattedTime()

        override fun getAgendaItemType() = AgendaItemType.TASK
    }

    data class ReminderUi(val reminder: Reminder): AgendaItemUi() {
        override val id = reminder.id
        override val title = reminder.title
        override val description = reminder.description

        override fun getFormattedTime() = reminder.getFormattedTime()

        override fun getAgendaItemType() = AgendaItemType.REMINDER
    }

    data object NeedleUi: AgendaItemUi()
}
