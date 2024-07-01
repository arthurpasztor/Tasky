package com.example.tasky.agenda.presentation

import android.app.NotificationManager
import android.content.Context
import android.os.SystemClock
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.tasky.MyApplication
import com.example.tasky.R

class NotificationSchedulerWorker(
    private val context: Context,
    private val params: WorkerParameters
): CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val title = params.inputData.getString(NOTIFICATION_TITLE) ?: ""
        val description = params.inputData.getString(NOTIFICATION_DESCRIPTION) ?: ""

        showNotification(context, title, description)

        return Result.success()
    }

    private fun showNotification(context: Context, title: String, description: String) {
        val notification = NotificationCompat.Builder(context, MyApplication.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(description)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = SystemClock.uptimeMillis().toInt()
        notificationManager.notify(notificationId, notification)
    }

    companion object {
        const val NOTIFICATION_TITLE = "NotificationTitle"
        const val NOTIFICATION_DESCRIPTION = "NotificationDescription"
    }
}