package com.example.tasky.auth.domain

import android.util.Patterns

fun String.isEmailValid() = Patterns.EMAIL_ADDRESS.matcher(this).matches()

fun String.isNameValid() = length in 4..50

fun String.isPasswordValid(): Boolean {
    val hasLowerCase = any { it.isLowerCase() }
    val hasUpperCase = any { it.isUpperCase() }
    val hasDigit = any { it.isDigit() }
    val hasValidLength = length !in 0..8

    return hasLowerCase && hasUpperCase && hasDigit && hasValidLength
}