package com.example.tasky.agenda.presentation.workmanager

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.tasky.agenda.presentation.notification.NotificationHandler
import com.example.tasky.agenda.presentation.notification.NotificationHandlerImpl

class NotificationSchedulerWorker(
    private val context: Context,
    private val params: WorkerParameters
) : CoroutineWorker(context, params), NotificationHandler by NotificationHandlerImpl() {

    override suspend fun doWork(): Result {
        val id = params.inputData.getString(NOTIFICATION_AGENDA_ITEM_ID) ?: ""
        val type = params.inputData.getString(NOTIFICATION_AGENDA_ITEM_TYPE) ?: ""
        val title = params.inputData.getString(NOTIFICATION_TITLE) ?: ""
        val description = params.inputData.getString(NOTIFICATION_DESCRIPTION) ?: ""

        showNotification(context, id, type, title, description)

        return Result.success()
    }

    companion object {
        const val NOTIFICATION_AGENDA_ITEM_ID = "NotificationAgendaItemId"
        const val NOTIFICATION_AGENDA_ITEM_TYPE = "NotificationAgendaItemType"
        const val NOTIFICATION_TITLE = "NotificationTitle"
        const val NOTIFICATION_DESCRIPTION = "NotificationDescription"
    }
}
