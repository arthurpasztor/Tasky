package com.example.tasky.agenda.presentation

import com.example.tasky.agenda.domain.isToday
import com.example.tasky.agenda.domain.model.Agenda
import com.example.tasky.agenda.domain.model.AgendaListItem
import com.example.tasky.agenda.domain.model.AgendaListItem.Reminder
import com.example.tasky.agenda.domain.model.AgendaListItem.Task

fun Agenda.toAgendaItemUiList(): List<AgendaItemUi> {
    var agendaItemUiList = items.map {
        when (it) {
            is Reminder -> AgendaItemUi.ReminderUi(it)
            is Task -> AgendaItemUi.TaskUi(it)
        }
    }

    if (isToday()) {
        agendaItemUiList = addNeedleItem(agendaItemUiList)
    }

    return agendaItemUiList
}

private fun Agenda.isToday(): Boolean {
    if (items.isEmpty()) return false

    return items.first().time.toLocalDate().isToday()
}

private fun Agenda.addNeedleItem(list: List<AgendaItemUi>): List<AgendaItemUi> {
    if (items.isEmpty()) return emptyList()

    val mutableList = list.toMutableList()

    if (items.first().isAfterNow()) {
        mutableList.add(0, AgendaItemUi.NeedleUi)
    } else if (items.last().isBeforeNow()) {
        mutableList.add(AgendaItemUi.NeedleUi)
    } else if (items.size > 1) {
        for (i in 0 until items.size - 1) {
            val j = i + 1

            if (items[i].isBeforeNow() && items[j].isAfterNow()) {
                mutableList.add(i + 1, AgendaItemUi.NeedleUi)
                break
            }
        }
    }

    return mutableList.toList()
}

fun Agenda.copyAgenda(): Agenda {
    val newList = mutableListOf<AgendaListItem>().apply { addAll(items) }
    return Agenda(newList)
}

fun getAgendaSample() = Agenda.getSample().toAgendaItemUiList()

fun getTaskSample() = AgendaItemUi.TaskUi(Task.getSampleTask())

fun getReminderSample() = AgendaItemUi.ReminderUi(Reminder.getSampleReminder())