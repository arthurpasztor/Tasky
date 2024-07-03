package com.example.tasky.agenda.data

import androidx.work.WorkManager
import com.example.tasky.agenda.domain.AgendaSyncManager
import com.example.tasky.agenda.presentation.workmanager.startPeriodicAgendaSync

class AgendaSyncManagerImpl(private val workManager: WorkManager): AgendaSyncManager {
    override fun startPeriodicAgendaSync() {
        workManager.startPeriodicAgendaSync()
    }
}