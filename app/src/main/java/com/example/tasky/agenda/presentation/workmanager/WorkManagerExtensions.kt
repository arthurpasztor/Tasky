package com.example.tasky.agenda.presentation.workmanager

import android.util.Log
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.tasky.agenda.domain.model.AgendaListItem
import java.time.Duration
import java.time.LocalDateTime
import java.util.UUID
import java.util.concurrent.TimeUnit

private const val TAG = "WorkManager"

fun WorkManager.scheduleNotification(agendaItem: AgendaListItem) {
    val now = LocalDateTime.now()
    if (agendaItem.remindAt.isAfter(now)) {
        val delayInMinutes = Duration.between(now, agendaItem.remindAt).abs().toMinutes()

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

fun WorkManager.cancelNotificationScheduler(itemId: String) {
    cancelUniqueWork(itemId)
    Log.i(TAG, "Notification with unique name $itemId canceled")
}

