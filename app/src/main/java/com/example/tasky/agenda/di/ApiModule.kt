package com.example.tasky.agenda.di

import com.example.tasky.agenda.data.AgendaRepositoryImpl
import com.example.tasky.agenda.data.AuthRepositoryImpl
import com.example.tasky.agenda.data.EventRepositoryImpl
import com.example.tasky.agenda.data.ReminderRepositoryImpl
import com.example.tasky.agenda.data.TaskRepositoryImpl
import com.example.tasky.agenda.data.db.EventDataSource
import com.example.tasky.agenda.data.db.EventDataSourceImpl
import com.example.tasky.agenda.data.db.ReminderDataSource
import com.example.tasky.agenda.data.db.ReminderDataSourceImpl
import com.example.tasky.agenda.data.db.TaskDataSource
import com.example.tasky.agenda.data.db.TaskDataSourceImpl
import com.example.tasky.agenda.domain.AgendaRepository
import com.example.tasky.agenda.domain.AuthRepository
import com.example.tasky.agenda.domain.EventRepository
import com.example.tasky.agenda.domain.ReminderRepository
import com.example.tasky.agenda.domain.TaskRepository
import com.example.tasky.agenda.presentation.AgendaDetailsViewModel
import com.example.tasky.agenda.presentation.AgendaViewModel
import com.example.tasky.db.TaskyDatabase
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val apiModule = module {
    viewModelOf(::AgendaViewModel)
    viewModel { params ->
        AgendaDetailsViewModel(get(), get(), get(), get(), params[0], params[1], params[2])
    }

    singleOf(::AuthRepositoryImpl).bind<AuthRepository>()
    singleOf(::AgendaRepositoryImpl).bind<AgendaRepository>()
    singleOf(::EventRepositoryImpl).bind<EventRepository>()
    singleOf(::TaskRepositoryImpl).bind<TaskRepository>()
    singleOf(::ReminderRepositoryImpl).bind<ReminderRepository>()

    single { TaskyDatabase(get(), get()) }
    single<EventDataSource> { EventDataSourceImpl(get()) }
    single<TaskDataSource> { TaskDataSourceImpl(get()) }
    single<ReminderDataSource> { ReminderDataSourceImpl(get()) }
}