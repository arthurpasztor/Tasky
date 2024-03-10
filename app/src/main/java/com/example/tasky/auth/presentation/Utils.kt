package com.example.tasky.auth.presentation

import android.util.Patterns

fun String.isEmailValid() = Patterns.EMAIL_ADDRESS.matcher(this).matches()