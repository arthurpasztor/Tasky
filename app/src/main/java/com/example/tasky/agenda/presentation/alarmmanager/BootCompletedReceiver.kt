package com.example.tasky.agenda.presentation.alarmmanager

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.tasky.agenda.domain.AgendaAlarmScheduler
import org.koin.java.KoinJavaComponent.inject

class BootCompletedReceiver : BroadcastReceiver() {

    private val TAG = "BootCompletedReceiver"

    private val alarmManagerScheduler: AgendaAlarmScheduler by inject(AlarmManagerSchedulerImpl::class.java)

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.i(TAG, "onReceive: BOOT_COMPLETED, start scheduling notifications")
            alarmManagerScheduler.scheduleAllNotificationsAfterReboot()
        }
    }
}