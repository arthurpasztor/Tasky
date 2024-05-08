package com.example.tasky.main.domain

import java.time.LocalDate
import java.time.LocalTime
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
