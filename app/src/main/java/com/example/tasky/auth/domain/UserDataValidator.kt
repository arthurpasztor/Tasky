package com.example.tasky.auth.domain

import android.util.Patterns

fun String.validateName(): Result<Unit, NameError> {
    return when {
        length < 4 -> Result.Error(NameError.TOO_SHORT)
        length > 50 -> Result.Error(NameError.TOO_LONG)
        else -> Result.Success(Unit)
    }
}

fun String.isEmailValid() = Patterns.EMAIL_ADDRESS.matcher(this).matches()

fun String.validatePassword(): Result<Unit, PasswordError> {
    return when {
        length in 0..8 -> Result.Error(PasswordError.TOO_SHORT)
        none { it.isLowerCase() } -> Result.Error(PasswordError.NO_LOWERCASE)
        none { it.isUpperCase() } -> Result.Error(PasswordError.NO_UPPERCASE)
        none { it.isDigit() } -> Result.Error(PasswordError.NO_DIGIT)
        else -> Result.Success(Unit)
    }
}

enum class NameError: Error {
    TOO_SHORT,
    TOO_LONG
}

enum class PasswordError: Error {
    NO_LOWERCASE,
    NO_UPPERCASE,
    NO_DIGIT,
    TOO_SHORT
}