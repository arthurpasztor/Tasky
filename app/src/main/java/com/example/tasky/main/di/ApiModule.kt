package com.example.tasky.main.di

import com.example.tasky.main.data.AgendaRepositoryImpl
import com.example.tasky.main.domain.AuthRepository
import com.example.tasky.main.data.AuthRepositoryImpl
import com.example.tasky.main.data.ReminderRepositoryImpl
import com.example.tasky.main.data.TaskRepositoryImpl
import com.example.tasky.main.domain.AgendaRepository
import com.example.tasky.main.domain.ReminderRepository
import com.example.tasky.main.domain.TaskRepository
import com.example.tasky.main.presentation.AgendaViewModel
import com.example.tasky.main.presentation.TaskReminderViewModel
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