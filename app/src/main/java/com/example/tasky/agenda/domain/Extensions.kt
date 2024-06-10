package com.example.tasky.agenda.domain

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

fun String.getInitials(): String {
    val delimiter = " "

    return this.trim().run {
        when {
            isEmpty() -> "-"

            length in 1..2 -> this.uppercase(Locale.getDefault())

            !contains(delimiter) -> {
                substring(0..1)
                    .uppercase(Locale.getDefault())
            }

            else -> {
                val initials = splitToSequence(delimiter).map { it.first() }
                sequenceOf(initials.first(), initials.last())
                    .joinToString("")
                    .uppercase(Locale.getDefault())
            }
        }
    }
}

fun LocalDate.formatHeaderDate(): String = format(DateTimeFormatter.ofPattern("dd MMMM yyyy")).uppercase()

fun LocalDate.formatDetailDate(): String = format(DateTimeFormatter.ofPattern("MMM dd yyyy"))

fun LocalTime.formatDetailTime(): String = format(DateTimeFormatter.ofPattern("HH:mm"))

fun LocalDateTime.getMillis(): Long = atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

fun LocalDate.getUTCMillis(): Long = atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()

fun Long.getLocalDateTimeFromMillis(): LocalDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(this), ZoneId.systemDefault())

fun LocalDateTime.formatAgendaDateTime(): String = format(DateTimeFormatter.ofPattern("MMM d, HH:mm"))

fun LocalDate.isToday() = this == LocalDate.now()