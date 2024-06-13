package com.example.tasky.agenda.domain.model

data class Agenda(val items: MutableList<AgendaListItem> = mutableListOf()) {

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