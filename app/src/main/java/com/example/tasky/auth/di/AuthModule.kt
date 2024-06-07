package com.example.tasky.auth.di

import com.example.tasky.auth.domain.AuthRepository
import com.example.tasky.auth.data.AuthRepositoryImpl
import com.example.tasky.auth.presentation.LoginViewModel
import com.example.tasky.auth.presentation.SignUpViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val authModule = module {
    viewModelOf(::LoginViewModel)
    viewModelOf(::SignUpViewModel)

    singleOf(::AuthRepositoryImpl).bind<AuthRepository>()
}