package com.example.tasky.auth.domain

data class Login(
    val email: String,
    val password: String
)

data class SignUp(
    val fullName: String,
    val email: String,
    val password: String
)