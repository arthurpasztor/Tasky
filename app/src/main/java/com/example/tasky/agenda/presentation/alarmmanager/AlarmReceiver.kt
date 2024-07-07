package com.example.tasky.agenda.presentation.alarmmanager

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.tasky.agenda.presentation.notification.NotificationHandler
import com.example.tasky.agenda.presentation.notification.NotificationHandlerImpl

class AlarmReceiver: BroadcastReceiver(), NotificationHandler by NotificationHandlerImpl() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val id = intent?.getStringExtra(ALARM_MGR_AGENDA_ITEM_ID) ?: return
        val type = intent.getStringExtra(ALARM_MGR_AGENDA_ITEM_TYPE) ?: return
        val title = intent.getStringExtra(ALARM_MGR_TITLE) ?: return
        val description = intent.getStringExtra(ALARM_MGR_DESCRIPTION) ?: return

        context?.let { showNotification(it, id, type, title, description) }
    }

    companion object {
        const val ALARM_MGR_AGENDA_ITEM_ID = "NotificationAgendaItemId"
        const val ALARM_MGR_AGENDA_ITEM_TYPE = "NotificationAgendaItemType"
        const val ALARM_MGR_TITLE = "NotificationTitle"
        const val ALARM_MGR_DESCRIPTION = "NotificationDescription"
    }
}