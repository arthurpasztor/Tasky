package com.example.tasky.core.di

import com.example.tasky.core.data.Preferences
import com.example.tasky.core.presentation.RootViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val coreModule = module {
    viewModelOf(::RootViewModel)

    single { Preferences(androidApplication()) }
}