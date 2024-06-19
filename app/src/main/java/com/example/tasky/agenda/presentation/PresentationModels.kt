package com.example.tasky.agenda.presentation

import com.example.tasky.agenda.domain.AgendaItemType
import com.example.tasky.agenda.domain.model.AgendaListItem.Event
import com.example.tasky.agenda.domain.model.AgendaListItem.Reminder
import com.example.tasky.agenda.domain.model.AgendaListItem.Task

enum class AgendaItemUiType {
    EVENT,
    TASK,
    REMINDER,
    NEEDLE;

    fun toAgendaItemType(): AgendaItemType? = when (this) {
        EVENT -> AgendaItemType.EVENT
        TASK -> AgendaItemType.TASK
        REMINDER -> AgendaItemType.REMINDER
        NEEDLE -> null
    }
}

sealed class AgendaItemUi {
    // Initializers needed because some classes (ex. NeedleUi) don't override the fields
    open val id: String = ""
    open val title: String = ""
    open val description: String = ""
    open val isDone: Boolean = false

    open fun getFormattedTime(): String = ""

    abstract fun getAgendaItemType(): AgendaItemUiType

    data class EventUi(val event: Event): AgendaItemUi() {
        override val id = event.id
        override val title = event.title
        override val description = event.description

        override fun getFormattedTime() = event.getFormattedTime()

        override fun getAgendaItemType() = AgendaItemUiType.EVENT
    }

    data class TaskUi(val task: Task): AgendaItemUi() {
        override val id = task.id
        override val title = task.title
        override val description = task.description
        override val isDone = task.isDone

        override fun getFormattedTime() = task.getFormattedTime()

        override fun getAgendaItemType() = AgendaItemUiType.TASK
    }

    data class ReminderUi(val reminder: Reminder): AgendaItemUi() {
        override val id = reminder.id
        override val title = reminder.title
        override val description = reminder.description

        override fun getFormattedTime() = reminder.getFormattedTime()

        override fun getAgendaItemType() = AgendaItemUiType.REMINDER
    }

    data object NeedleUi: AgendaItemUi() {
        override fun getAgendaItemType(): AgendaItemUiType = AgendaItemUiType.NEEDLE
    }
}
