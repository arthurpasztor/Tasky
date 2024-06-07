package com.example.tasky.auth.presentation

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.annotation.StringRes
import com.example.tasky.auth.domain.asUiText
import com.example.tasky.core.domain.DataError

fun Context.showToast(@StringRes id: Int) {
    val errorMessage = getString(id)
    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
}

fun Context.showToast(error: DataError, tag: String) {
    val errorMessage = error.asUiText().asString(this)
    Log.e(tag, "Error: $errorMessage")
    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
}
