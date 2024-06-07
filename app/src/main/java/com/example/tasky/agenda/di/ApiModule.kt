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
import com.example.tasky.agenda.presentation.TaskReminderViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val apiModule = module {
    viewModelOf(::AgendaViewModel)
    viewModel { params ->
        TaskReminderViewModel(get(), get(), params[0], params[1])
    }

    single<AuthRepository> { AuthRepositoryImpl(get()) }
    single<TaskRepository> { TaskRepositoryImpl(get()) }
    single<ReminderRepository> { ReminderRepositoryImpl(get()) }
    single<AgendaRepository> { AgendaRepositoryImpl(get()) }
}