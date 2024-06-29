package com.example.tasky

import android.app.Application
import com.example.tasky.auth.di.authModule
import com.example.tasky.core.di.coreModule
import com.example.tasky.agenda.di.apiModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MyApplication : Application() {
    val applicationScope = CoroutineScope(SupervisorJob())

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MyApplication)
            androidLogger()
            modules(coreModule, authModule, apiModule)
        }
    }
}