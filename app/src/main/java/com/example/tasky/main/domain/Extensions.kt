package com.example.tasky.main.domain

import java.util.Locale

fun String.getInitials(): String {
    val delimiter = " "

    return this.trim().run {
        when {
            isEmpty() -> "-"
            length == 1 || length == 2 -> this.uppercase(Locale.getDefault())
            !contains(delimiter) -> {
                substring(0..1)
                    .uppercase(Locale.getDefault())
            }

            else -> {
                splitToSequence(delimiter, limit = 2)
                    .map { it.first() }
                    .joinToString("")
                    .uppercase(Locale.getDefault())
            }
        }
    }
}
