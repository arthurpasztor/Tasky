package com.example.tasky.agenda.domain

import kotlinx.coroutines.flow.Flow

interface NetworkConnectivityMonitor {
    fun isNetworkAvailable(): Boolean

    fun observeNetworkAvailability(): Flow<NetworkState>

    enum class NetworkState {
        Available, Unavailable;

        fun isAvailable() = this == Available
    }
}