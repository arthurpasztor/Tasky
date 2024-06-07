package com.example.tasky.agenda.domain.model

data class Agenda(val items: List<AgendaListItem> = emptyList()) {
    companion object {
        fun getEmpty() = Agenda()

        fun getSample() = Agenda(
            listOf(
                AgendaListItem.Task.getSampleTask(),
                AgendaListItem.Reminder.getSampleReminder()
            )
        )
    }
}