package com.example.tasky.auth.di

import com.example.tasky.auth.data.AuthRepository
import com.example.tasky.auth.data.AuthRepositoryImpl
import com.example.tasky.core.data.HttpClientFactory
import com.example.tasky.auth.presentation.LoginViewModel
import com.example.tasky.auth.presentation.SignUpViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val authModule = module {
    viewModel {
        LoginViewModel(get())
    }
    viewModel {
        SignUpViewModel(get())
    }

    single(named("authClient")) { HttpClientFactory.provideAuthClient() }

    single<AuthRepository> { AuthRepositoryImpl(get(named("authClient")), get()) }
}