package com.example.tasky.agenda.di

import com.example.tasky.agenda.data.AgendaRepositoryImpl
import com.example.tasky.agenda.domain.AuthRepository
import com.example.tasky.agenda.data.AuthRepositoryImpl
import com.example.tasky.agenda.data.ReminderRepositoryImpl
import com.example.tasky.agenda.data.TaskRepositoryImpl
import com.example.tasky.agenda.domain.AgendaRepository
import com.example.tasky.agenda.domain.ReminderRepository
import com.example.tasky.agenda.domain.TaskRepository
import com.example.tasky.agenda.presentation.AgendaViewModel
import com.example.tasky.agenda.presentation.AgendaDetailsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val apiModule = module {
    viewModelOf(::AgendaViewModel)
    viewModel { params ->
        AgendaDetailsViewModel(get(), get(), get(), params[0], params[1], params[2])
    }

    singleOf(::AuthRepositoryImpl).bind<AuthRepository>()
    singleOf(::AgendaRepositoryImpl).bind<AgendaRepository>()
    singleOf(::TaskRepositoryImpl).bind<TaskRepository>()
    singleOf(::ReminderRepositoryImpl).bind<ReminderRepository>()
}