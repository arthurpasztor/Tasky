package com.example.tasky.core.di

import com.example.tasky.core.data.Preferences
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val coreModule = module {

    single { Preferences(androidApplication()) }
}