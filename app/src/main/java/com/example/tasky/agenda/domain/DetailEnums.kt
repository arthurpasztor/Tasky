package com.example.tasky.agenda.domain

import java.time.Duration
import java.time.LocalDateTime

enum class AgendaItemType {
    EVENT,
    TASK,
    REMINDER
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

    companion object {
        fun getReminderType(reference: LocalDateTime, reminder: LocalDateTime): ReminderType {
            val diffInMinutes = Duration.between(reference, reminder).abs().toMinutes()

            return when (diffInMinutes) {
                10L -> MINUTES_10
                30L -> MINUTES_30
                60L -> HOUR_1
                360L -> HOUR_6
                1440L -> DAY_1
                else -> DAY_1
            }
        }
    }
}

enum class DetailItemType {
    TITLE,
    DESCRIPTION
}