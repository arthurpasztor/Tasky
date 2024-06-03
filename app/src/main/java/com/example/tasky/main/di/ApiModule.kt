package com.example.tasky.main.di

import com.example.tasky.main.data.ApiRepository
import com.example.tasky.main.data.ApiRepositoryImpl
import com.example.tasky.core.data.HttpClientFactory
import com.example.tasky.main.presentation.AgendaViewModel
import com.example.tasky.main.presentation.TaskReminderViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val apiModule = module {
    viewModelOf(::AgendaViewModel)
    viewModel { params ->
        TaskReminderViewModel(get(), params[0], params[1])
    }

    single(named("apiClient")) { HttpClientFactory.provideApiClient() }

    single<ApiRepository> { ApiRepositoryImpl(get(named("authClient"))) }
}