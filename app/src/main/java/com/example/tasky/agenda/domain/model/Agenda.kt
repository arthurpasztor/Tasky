package com.example.tasky.agenda.domain.model

data class Agenda(val items: MutableList<AgendaListItem> = mutableListOf()) {

    fun setTaskDone(task: AgendaListItem.Task) {
        items.forEachIndexed { index, agendaListItem ->
            if (agendaListItem is AgendaListItem.Task && agendaListItem.id == task.id) {
                items[index] = task
            }
        }
    }

    companion object {
        fun getEmpty() = Agenda()

        fun getSample() = Agenda(
            mutableListOf(
                AgendaListItem.Task.getSampleTask(),
                AgendaListItem.Reminder.getSampleReminder()
            )
        )
    }
}