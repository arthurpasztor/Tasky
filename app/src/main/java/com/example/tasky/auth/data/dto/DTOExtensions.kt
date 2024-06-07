package com.example.tasky.auth.data.dto

import com.example.tasky.auth.domain.Login
import com.example.tasky.auth.domain.SignUp

fun LoginDTO.toLogin() = Login(email, password)

fun Login.toLoginDTO() = LoginDTO(email, password)

fun SignUp.toSignUpDTO() = SignUpDTO(fullName, email, password)