package com.example.tasky.agenda.presentation.notification

import android.Manifest
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.content.ContextCompat
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
            val deepLinkUri = context.getString(R.string.deep_link, type, agendaItemId)
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(deepLinkUri))
            val pendingIntent = TaskStackBuilder.create(context).run {
                addNextIntentWithParentStack(intent)
                getPendingIntent(
                    agendaItemId.hashCode(),
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            }

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
