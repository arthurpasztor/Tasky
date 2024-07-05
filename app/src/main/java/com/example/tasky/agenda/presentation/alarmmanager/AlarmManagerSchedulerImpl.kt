package com.example.tasky.agenda.presentation.alarmmanager

import android.app.AlarmManager
import android.content.Context
import com.example.tasky.agenda.domain.AgendaAlarmScheduler
import com.example.tasky.agenda.domain.model.AgendaListItem
import com.example.tasky.core.data.Preferences

class AlarmManagerSchedulerImpl(
    private val context: Context,
    private val prefs: Preferences,
    private val alarmManager: AlarmManager
): AgendaAlarmScheduler {

    override fun scheduleNotification(agendaItem: AgendaListItem) {
        alarmManager.scheduleNotification(context, prefs, agendaItem)
    }

    override fun cancelNotificationScheduler(agendaItemId: String) {
        alarmManager.cancelNotificationScheduler(context, prefs, agendaItemId)
    }

    override fun cancelAllNotificationSchedulers() {
        alarmManager.cancelAllNotificationSchedulers(context, prefs)
    }
}