package com.example.tasky.agenda.presentation.workmanager

import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.tasky.agenda.domain.model.AgendaListItem
import com.example.tasky.agenda.domain.model.AgendaListItem.Event
import com.example.tasky.core.data.Preferences
import org.koin.java.KoinJavaComponent.inject
import java.time.Duration
import java.time.LocalDateTime
import java.util.UUID
import java.util.concurrent.TimeUnit

private const val TAG = "WorkManager"

private const val AGENDA_SYNC_ID = "agendaSyncId"
private const val AGENDA_SYNC_PERIOD_MINUTES = 30L

fun WorkManager.scheduleNotification(agendaItem: AgendaListItem) {

    val now = LocalDateTime.now()

    val reminderTime = if (agendaItem.isCurrentUserAsAttendeeInEvent()) {
        agendaItem.getCurrentUsersPersonalReminder()
    } else {
        agendaItem.remindAt
    }

    if (reminderTime.isAfter(now)) {
        val delayInMinutes = Duration.between(now, reminderTime).abs().toMinutes()

        val request = OneTimeWorkRequestBuilder<NotificationSchedulerWorker>()
            .setInputData(
                workDataOf(
                    NotificationSchedulerWorker.NOTIFICATION_AGENDA_ITEM_ID to agendaItem.id,
                    NotificationSchedulerWorker.NOTIFICATION_AGENDA_ITEM_TYPE to agendaItem.getItemType().name,
                    NotificationSchedulerWorker.NOTIFICATION_TITLE to agendaItem.title,
                    NotificationSchedulerWorker.NOTIFICATION_DESCRIPTION to agendaItem.description
                )
            )
            .setInitialDelay(delayInMinutes, TimeUnit.MINUTES)
            .setId(UUID.fromString(agendaItem.id))
            .build()

        enqueueUniqueWork(agendaItem.id, ExistingWorkPolicy.REPLACE, request)
        Log.i(TAG, "Notification with unique name ${agendaItem.id} enqueued")
    }
}

private fun AgendaListItem.isCurrentUserAsAttendeeInEvent() = this is Event && !isUserEventCreator

private fun AgendaListItem.getCurrentUsersPersonalReminder(): LocalDateTime {
    val event = this as? Event ?: return remindAt

    val prefs: Preferences by inject(Preferences::class.java)
    val currentUserId = prefs.getEncryptedString(Preferences.KEY_USER_ID, "")

    val currentUserAsAttendee = event.attendees.firstOrNull { it.userId == currentUserId }
    return currentUserAsAttendee?.remindAt ?: event.remindAt
}

fun WorkManager.cancelNotificationScheduler(itemId: String) {
    cancelUniqueWork(itemId)
    Log.i(TAG, "Notification with unique name $itemId canceled")
}

fun WorkManager.startPeriodicAgendaSync() {
    val work = PeriodicWorkRequestBuilder<PeriodicFullSyncWorker>(Duration.ofMinutes(AGENDA_SYNC_PERIOD_MINUTES))
        .setInitialDelay(AGENDA_SYNC_PERIOD_MINUTES, TimeUnit.MINUTES)
        .setConstraints(Constraints(requiredNetworkType = NetworkType.CONNECTED))
        .build()

    enqueueUniquePeriodicWork(AGENDA_SYNC_ID, ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE, work)
    Log.i(TAG, "Periodic full agenda sync $AGENDA_SYNC_ID enqueued")
}
