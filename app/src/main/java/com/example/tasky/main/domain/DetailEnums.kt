package com.example.tasky.main.domain

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
    DAY_1("1 day before")
}

enum class DetailItemType {
    TITLE,
    DESCRIPTION
}