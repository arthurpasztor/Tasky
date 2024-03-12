package com.example.tasky.auth.presentation

import android.util.Patterns

fun String.isEmailValid() = Patterns.EMAIL_ADDRESS.matcher(this).matches()

fun String.isNameValid(): Boolean {
    return !(length < 4 || length > 50)
}

fun String.isPasswordValid(): Boolean {
    if (length < 9) return false

    if (!matches(Regex(".*[A-Z].*"))) return false

    if (!matches(Regex(".*[a-z].*"))) return false

    if (!matches(Regex(".*\\d.*"))) return false

    return true
}