package com.example.tasky.agenda.presentation

import com.example.tasky.agenda.domain.AgendaDM
import com.example.tasky.agenda.domain.AgendaListItem.ReminderDM
import com.example.tasky.agenda.domain.AgendaListItem.TaskDM

fun AgendaDM.toAgendaItemUiList(): List<AgendaItemUi> {
    return items.map {
        when (it) {
            is ReminderDM -> AgendaItemUi.ReminderUi(it)
            is TaskDM -> AgendaItemUi.TaskUi(it)
        }
    }
}

fun getAgendaSample() = AgendaDM.getSample().toAgendaItemUiList()

fun getTaskSample() = AgendaItemUi.TaskUi(TaskDM.getSampleTask())

fun getReminderSample() = AgendaItemUi.ReminderUi(ReminderDM.getSampleReminder())