package com.example.tasky.agenda.presentation.workmanager

import android.Manifest
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
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
        val isNotificationPermissionGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }

        if (isNotificationPermissionGranted) {
            val intent = Intent(context, MainActivity::class.java).apply {
                putExtras(bundleOf(AGENDA_ITEM_ID to agendaItemId, AGENDA_ITEM_TYPE to type))
            }
            val pendingIntent =
                PendingIntent.getActivity(
                    context,
                    agendaItemId.hashCode(),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

            val notification = NotificationCompat.Builder(context, MyApplication.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baby_changing_station_24)
                .setContentTitle(title)
                .setContentText(description)
                .setContentIntent(pendingIntent)
                .build()

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notificationId = UUID.fromString(agendaItemId).hashCode()
            notificationManager.notify(notificationId, notification)
        }
    }
}

const val AGENDA_ITEM_ID = "agenda_item_id"
const val AGENDA_ITEM_TYPE = "agenda_item_type"
