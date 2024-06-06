package com.example.tasky.auth.domain

data class LoginDM(
    val email: String,
    val password: String
)

data class SignUpDM(
    val fullName: String,
    val email: String,
    val password: String
)