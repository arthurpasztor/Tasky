package com.example.tasky.agenda.presentation.alarmmanager

import android.app.AlarmManager
import android.content.Context
import com.example.tasky.agenda.data.db.EventDataSource
import com.example.tasky.agenda.data.db.ReminderDataSource
import com.example.tasky.agenda.data.db.TaskDataSource
import com.example.tasky.agenda.data.dto.toEvent
import com.example.tasky.agenda.data.dto.toReminder
import com.example.tasky.agenda.data.dto.toTask
import com.example.tasky.agenda.domain.AgendaAlarmScheduler
import com.example.tasky.agenda.domain.model.AgendaListItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class AlarmManagerSchedulerImpl(
    private val context: Context,
    private val applicationScope: CoroutineScope,
    private val localEventDataSource: EventDataSource,
    private val localTaskDataSource: TaskDataSource,
    private val localReminderDataSource: ReminderDataSource,
    private val alarmManager: AlarmManager
): AgendaAlarmScheduler {

    override fun scheduleNotification(agendaItem: AgendaListItem) {
        alarmManager.scheduleNotification(context, agendaItem)
    }

    override fun scheduleAllNotificationsAfterReboot() {
        // Access Agenda items from local database
        applicationScope.launch {
            localEventDataSource.getAllEvents().map { it.toEvent() }.forEach {
                alarmManager.scheduleNotification(context, it)
            }
            localTaskDataSource.getAllTasks().map { it.toTask() }.forEach {
                alarmManager.scheduleNotification(context, it)
            }
            localReminderDataSource.getAllReminders().map { it.toReminder() }.forEach {
                alarmManager.scheduleNotification(context, it)
            }
        }
    }

    override fun cancelNotificationScheduler(agendaItemId: String) {
        alarmManager.cancelNotificationScheduler(context, agendaItemId)
    }

    override fun cancelAllNotificationSchedulers() {
        applicationScope.launch {
            localEventDataSource.getAllEvents().forEach {
                alarmManager.cancelNotificationScheduler(context, it.id)
            }
            localTaskDataSource.getAllTasks().forEach {
                alarmManager.cancelNotificationScheduler(context, it.id)
            }
            localReminderDataSource.getAllReminders().forEach {
                alarmManager.cancelNotificationScheduler(context, it.id)
            }
        }
    }
}