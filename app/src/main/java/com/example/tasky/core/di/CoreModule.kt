package com.example.tasky.core.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.example.tasky.agenda.data.db.getListOfItemAdapter
import com.example.tasky.core.data.HttpClientFactory
import com.example.tasky.core.data.Preferences
import com.example.tasky.core.presentation.RootViewModel
import com.example.tasky.db.TaskyDatabase
import com.example.tasky.migrations.EventEntity
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val coreModule = module {
    viewModelOf(::RootViewModel)

    single { Preferences(androidApplication()) }

    single { HttpClientFactory.provideHttpClient() }

    single<SqlDriver> {
        AndroidSqliteDriver(
            schema = TaskyDatabase.Schema,
            context = androidApplication(),
            name = "tasky.db"
        )
    }

    single<EventEntity.Adapter> {
        EventEntity.Adapter(
            attendeesAdapter = getListOfItemAdapter(),
            photosAdapter = getListOfItemAdapter()
        )
    }
}