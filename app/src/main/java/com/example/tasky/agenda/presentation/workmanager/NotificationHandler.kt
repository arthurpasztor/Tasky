package com.example.tasky.agenda.presentation.workmanager

import android.app.NotificationManager
import android.content.Context
import android.os.SystemClock
import androidx.core.app.NotificationCompat
import com.example.tasky.MyApplication
import com.example.tasky.R

interface NotificationHandler {
    fun showNotification(context: Context, title: String, description: String)
}

class NotificationHandlerImpl : NotificationHandler {
    override fun showNotification(context: Context, title: String, description: String) {
        val notification = NotificationCompat.Builder(context, MyApplication.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(description)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = SystemClock.uptimeMillis().toInt()
        notificationManager.notify(notificationId, notification)
    }
}
