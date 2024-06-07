package com.example.tasky

import android.app.Application
import com.example.tasky.auth.di.authModule
import com.example.tasky.core.di.coreModule
import com.example.tasky.agenda.di.apiModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MyApplication)
            androidLogger()
            modules(coreModule, authModule, apiModule)
        }
    }
}