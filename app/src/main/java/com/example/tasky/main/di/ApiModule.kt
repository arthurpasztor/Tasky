package com.example.tasky.main.di

import com.example.tasky.main.data.ApiRepository
import com.example.tasky.main.data.ApiRepositoryImpl
import com.example.tasky.auth.data.AuthRepository
import com.example.tasky.auth.data.AuthRepositoryImpl
import com.example.tasky.core.data.NetworkWrapper
import com.example.tasky.core.data.Preferences
import com.example.tasky.auth.presentation.LoginViewModel
import com.example.tasky.auth.presentation.SignUpViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val apiModule = module {

    single(named("apiClient")) { NetworkWrapper.provideApiClient() }

    single<ApiRepository> { ApiRepositoryImpl(get(named("apiClient"))) }
}