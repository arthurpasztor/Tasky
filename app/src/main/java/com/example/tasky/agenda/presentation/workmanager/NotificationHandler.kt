package com.example.tasky.agenda.presentation.workmanager

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import androidx.core.app.NotificationCompat
import androidx.core.os.bundleOf
import com.example.tasky.MainActivity
import com.example.tasky.MyApplication
import com.example.tasky.R
import java.util.UUID

interface NotificationHandler {
    fun showNotification(context: Context, agendaItemId: String, type: String, title: String, description: String)
}

class NotificationHandlerImpl : NotificationHandler {
    override fun showNotification(
        context: Context,
        agendaItemId: String,
        type: String,
        title: String,
        description: String
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            putExtras(bundleOf(AGENDA_ITEM_ID to agendaItemId, AGENDA_ITEM_TYPE to type))
        }
        val pendingIntent = PendingIntent.getActivity(context, UUID.randomUUID().hashCode(), intent, PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(context, MyApplication.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_baby_changing_station_24)
            .setContentTitle(title)
            .setContentText(description)
            .setContentIntent(pendingIntent)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = SystemClock.uptimeMillis().toInt()
        notificationManager.notify(notificationId, notification)
    }
}

const val AGENDA_ITEM_ID = "agenda_item_id"
const val AGENDA_ITEM_TYPE = "agenda_item_type"
