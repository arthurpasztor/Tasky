package com.example.tasky.auth.di

import com.example.tasky.auth.data.ApiRepository
import com.example.tasky.auth.data.ApiRepositoryImpl
import com.example.tasky.auth.data.AuthRepository
import com.example.tasky.auth.data.AuthRepositoryImpl
import com.example.tasky.auth.data.NetworkWrapper
import com.example.tasky.auth.data.Preferences
import com.example.tasky.auth.presentation.LoginViewModel
import com.example.tasky.auth.presentation.SignUpViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val authModule = module {
    viewModelOf(::LoginViewModel)
    viewModelOf(::SignUpViewModel)

    single { Preferences(androidApplication()) }

    single(named("authClient")) { NetworkWrapper.provideAuthClient() }
    single(named("apiClient")) { NetworkWrapper.provideApiClient() }

    single<AuthRepository> { AuthRepositoryImpl(get(named("authClient"))) }
    single<ApiRepository> { ApiRepositoryImpl(get(named("apiClient"))) }
}