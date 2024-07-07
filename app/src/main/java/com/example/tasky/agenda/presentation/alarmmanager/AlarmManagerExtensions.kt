package com.example.tasky.agenda.presentation.alarmmanager

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import com.example.tasky.agenda.domain.getMillis
import com.example.tasky.agenda.domain.model.AgendaListItem
import com.example.tasky.agenda.presentation.alarmmanager.AlarmReceiver.Companion.ALARM_MGR_AGENDA_ITEM_ID
import com.example.tasky.agenda.presentation.alarmmanager.AlarmReceiver.Companion.ALARM_MGR_AGENDA_ITEM_TYPE
import com.example.tasky.agenda.presentation.alarmmanager.AlarmReceiver.Companion.ALARM_MGR_DESCRIPTION
import com.example.tasky.agenda.presentation.alarmmanager.AlarmReceiver.Companion.ALARM_MGR_TITLE
import java.time.LocalDateTime

private const val TAG = "AlarmManager"

@SuppressLint("MissingPermission")
fun AlarmManager.scheduleNotification(context: Context, agendaItem: AgendaListItem) {

    val now = LocalDateTime.now()

    val reminderTime = if (agendaItem.isCurrentUserAsAttendeeInEvent()) {
        agendaItem.getCurrentUsersPersonalReminder()
    } else {
        agendaItem.remindAt
    }

    val canScheduleExactAlarms = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        canScheduleExactAlarms()
    } else {
        true
    }
    if (reminderTime.isAfter(now) && canScheduleExactAlarms) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(ALARM_MGR_AGENDA_ITEM_ID, agendaItem.id)
            putExtra(ALARM_MGR_AGENDA_ITEM_TYPE, agendaItem.getItemType().name)
            putExtra(ALARM_MGR_TITLE, agendaItem.title)
            putExtra(ALARM_MGR_DESCRIPTION, agendaItem.description)
        }
        setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            reminderTime.getMillis(),
            PendingIntent.getBroadcast(
                context,
                agendaItem.id.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )

        Toast.makeText(context, "Notification ${agendaItem.title} scheduled", Toast.LENGTH_SHORT).show()

        Log.i(TAG, "Notification with unique name ${agendaItem.title} scheduled with AlarmManager")
    }
}

fun AlarmManager.cancelNotificationScheduler(context: Context, agendaItemId: String) {
    cancel(
        PendingIntent.getBroadcast(
            context,
            agendaItemId.hashCode(),
            Intent(),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    )
    Log.i(TAG, "Notification with unique name $agendaItemId canceled with AlarmManager")
}


