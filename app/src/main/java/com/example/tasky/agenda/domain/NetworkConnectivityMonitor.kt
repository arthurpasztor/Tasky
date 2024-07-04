package com.example.tasky.agenda.domain

interface NetworkConnectivityMonitor {
    fun isNetworkAvailable(): Boolean
}