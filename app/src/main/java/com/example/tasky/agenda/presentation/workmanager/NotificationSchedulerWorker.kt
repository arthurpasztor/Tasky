package com.example.tasky.agenda.presentation.workmanager

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class NotificationSchedulerWorker(
    private val context: Context,
    private val params: WorkerParameters
) : CoroutineWorker(context, params), NotificationHandler by NotificationHandlerImpl() {

    override suspend fun doWork(): Result {
        val title = params.inputData.getString(NOTIFICATION_TITLE) ?: ""
        val description = params.inputData.getString(NOTIFICATION_DESCRIPTION) ?: ""

        showNotification(context, title, description)

        return Result.success()
    }

    companion object {
        const val NOTIFICATION_TITLE = "NotificationTitle"
        const val NOTIFICATION_DESCRIPTION = "NotificationDescription"
    }
}
