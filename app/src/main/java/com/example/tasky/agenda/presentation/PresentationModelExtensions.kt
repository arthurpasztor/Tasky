package com.example.tasky.agenda.presentation

import com.example.tasky.agenda.domain.model.Agenda
import com.example.tasky.agenda.domain.model.AgendaListItem.Reminder
import com.example.tasky.agenda.domain.model.AgendaListItem.Task

fun Agenda.toAgendaItemUiList(): List<AgendaItemUi> {
    return items.map {
        when (it) {
            is Reminder -> AgendaItemUi.ReminderUi(it)
            is Task -> AgendaItemUi.TaskUi(it)
        }
    }
}

fun getAgendaSample() = Agenda.getSample().toAgendaItemUiList()

fun getTaskSample() = AgendaItemUi.TaskUi(Task.getSampleTask())

fun getReminderSample() = AgendaItemUi.ReminderUi(Reminder.getSampleReminder())