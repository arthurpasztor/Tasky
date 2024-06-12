package com.example.tasky.agenda.domain

import java.time.LocalDateTime

enum class AgendaItemType {
    TASK,
    REMINDER,
    UNKNOWN
}

enum class DetailInteractionMode {
    CREATE,
    EDIT,
    VIEW
}

enum class ReminderType {
    MINUTES_10,
    MINUTES_30,
    HOUR_1,
    HOUR_6,
    DAY_1;

    fun getReminder(reference: LocalDateTime): LocalDateTime {
        return when (this) {
            MINUTES_10 -> reference.minusMinutes(10)
            MINUTES_30 -> reference.minusMinutes(30)
            HOUR_1 -> reference.minusHours(1)
            HOUR_6 -> reference.minusHours(6)
            DAY_1 -> reference.minusDays(1)
        }
    }
}

enum class DetailItemType {
    TITLE,
    DESCRIPTION
}