package com.example.tasky.agenda.domain

import com.example.tasky.agenda.domain.model.AgendaListItem

interface AgendaAlarmScheduler {

    fun scheduleNotification(agendaItem: AgendaListItem)

    fun cancelNotificationScheduler(agendaItemId: String)

    fun cancelAllNotificationSchedulers()
}