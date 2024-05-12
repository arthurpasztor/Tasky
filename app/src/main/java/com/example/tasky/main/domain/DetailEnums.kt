package com.example.tasky.main.domain

import java.time.LocalDateTime

enum class AgendaItemType {
    TASK,
    REMINDER
}

enum class DetailInteractionMode {
    CREATE,
    EDIT,
    VIEW
}

enum class ReminderType(val display: String) {
    MINUTES_10("10 minutes before"),
    MINUTES_30("30 minutes before"),
    HOUR_1("1 hour before"),
    HOUR_6("6 hour before"),
    DAY_1("1 day before");

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