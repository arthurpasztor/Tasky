package com.example.tasky.agenda.presentation.alarmmanager

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.tasky.agenda.domain.getMillis
import com.example.tasky.agenda.domain.model.AgendaListItem
import com.example.tasky.agenda.presentation.alarmmanager.AlarmReceiver.Companion.ALARM_MGR_AGENDA_ITEM_ID
import com.example.tasky.agenda.presentation.alarmmanager.AlarmReceiver.Companion.ALARM_MGR_AGENDA_ITEM_TYPE
import com.example.tasky.agenda.presentation.alarmmanager.AlarmReceiver.Companion.ALARM_MGR_DESCRIPTION
import com.example.tasky.agenda.presentation.alarmmanager.AlarmReceiver.Companion.ALARM_MGR_TITLE
import com.example.tasky.core.data.Preferences
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId

private const val TAG = "AlarmManager"

@SuppressLint("MissingPermission")
fun AlarmManager.scheduleNotification(context: Context, prefs: Preferences, agendaItem: AgendaListItem) {

    val now = LocalDateTime.now()

    val reminderTime = if (agendaItem.isCurrentUserAsAttendeeInEvent()) {
        agendaItem.getCurrentUsersPersonalReminder()
    } else {
        agendaItem.remindAt
    }

    if (reminderTime.isAfter(now)) {
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

        prefs.putScheduledAgendaItemId(agendaItem.id)

        Log.i(TAG, "Notification with unique name ${agendaItem.id} scheduled with AlarmManager")
    }
}

fun AlarmManager.cancelNotificationScheduler(context: Context, prefs: Preferences, agendaItemId: String) {
    cancel(
        PendingIntent.getBroadcast(
            context,
            agendaItemId.hashCode(),
            Intent(),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    )

    prefs.removeScheduledAgendaItemId(agendaItemId)
    Log.i(TAG, "Notification with unique name $agendaItemId canceled with AlarmManager")
}

fun AlarmManager.cancelAllNotificationSchedulers(context: Context, prefs: Preferences) {
    prefs.getScheduledAgendaItemIds().forEach {
        cancelNotificationScheduler(context, prefs, it)
    }
    Log.i(TAG, "All notifications canceled with AlarmManager")
}

