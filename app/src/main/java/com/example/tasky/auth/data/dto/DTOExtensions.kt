package com.example.tasky.auth.data.dto

import com.example.tasky.auth.domain.LoginDM
import com.example.tasky.auth.domain.SignUpDM

fun LoginRequest.toLoginDM() = LoginDM(email, password)

fun LoginDM.toLoginRequest() = LoginRequest(email, password)

fun SignUpRequest.toSignUpDM() = SignUpDM(fullName, email, password)

fun SignUpDM.toSignUpRequest() = SignUpRequest(fullName, email, password)