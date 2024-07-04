package com.example.tasky.agenda.data

import android.content.Context
import android.net.ConnectivityManager
import com.example.tasky.agenda.domain.NetworkConnectivityMonitor

class NetworkConnectivityMonitorImpl(context: Context) : NetworkConnectivityMonitor {

    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    override fun isNetworkAvailable() = connectivityManager.activeNetwork != null
}