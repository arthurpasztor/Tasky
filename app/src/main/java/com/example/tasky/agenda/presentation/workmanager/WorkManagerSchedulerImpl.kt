package com.example.tasky.agenda.presentation.workmanager

import androidx.work.WorkManager
import com.example.tasky.agenda.domain.AgendaAlarmScheduler
import com.example.tasky.agenda.domain.model.AgendaListItem

class WorkManagerSchedulerImpl(private val workManager: WorkManager): AgendaAlarmScheduler {
    override fun scheduleNotification(agendaItem: AgendaListItem) {
        workManager.scheduleNotification(agendaItem)
    }

    override fun cancelNotificationScheduler(agendaItemId: String) {
        workManager.cancelNotificationScheduler(agendaItemId)
    }

    override fun cancelAllNotificationSchedulers() {
        workManager.cancelAllWork()
    }
}