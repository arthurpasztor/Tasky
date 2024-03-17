package com.example.tasky.auth.presentation

import android.os.Parcelable
import androidx.lifecycle.ViewModel
import com.example.tasky.auth.domain.isEmailValid
import com.example.tasky.auth.domain.isNameValid
import com.example.tasky.auth.domain.isPasswordValid
import kotlinx.parcelize.Parcelize

class SignUpViewModel : ViewModel() {

    val state = SignUpState()

    fun signUp() {
        // TODO implement
    }
}

@Parcelize
data class SignUpState(
    var nameText: String = "",
    var emailText: String = "",
    var passwordText: String = ""
): Parcelable {
    fun isNameValid() = nameText.isNameValid()

    fun isEmailValid() = emailText.isEmailValid()

    fun isPasswordValid() = passwordText.isPasswordValid()
}
