package com.example.tasky.core.di

import androidx.work.WorkManager
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.example.tasky.MyApplication
import com.example.tasky.agenda.data.db.getListOfItemAdapter
import com.example.tasky.agenda.data.db.getOfflineStatusAdapter
import com.example.tasky.core.data.HttpClientFactory
import com.example.tasky.core.data.Preferences
import com.example.tasky.core.presentation.RootViewModel
import com.example.tasky.db.TaskyDatabase
import com.example.tasky.migrations.EventEntity
import com.example.tasky.migrations.ReminderEntity
import com.example.tasky.migrations.TaskEntity
import kotlinx.coroutines.CoroutineScope
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
            photosAdapter = getListOfItemAdapter(),
            deletedPhotoKeysAdapter = getListOfItemAdapter(),
            offlineStatusAdapter = getOfflineStatusAdapter()
        )
    }

    single<TaskEntity.Adapter> {
        TaskEntity.Adapter(
            offlineStatusAdapter = getOfflineStatusAdapter()
        )
    }

    single<ReminderEntity.Adapter> {
        ReminderEntity.Adapter(
            offlineStatusAdapter = getOfflineStatusAdapter()
        )
    }

    single<CoroutineScope> { (androidApplication() as MyApplication).applicationScope }

    single<WorkManager> { WorkManager.getInstance(androidApplication()) }
}