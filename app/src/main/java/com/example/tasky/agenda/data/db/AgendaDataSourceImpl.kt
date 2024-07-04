package com.example.tasky.agenda.data.db

import com.example.tasky.agenda.data.dto.AgendaDTO
import com.example.tasky.agenda.data.dto.toEvent
import com.example.tasky.agenda.data.dto.toReminder
import com.example.tasky.agenda.data.dto.toTask
import com.example.tasky.agenda.domain.getFormattedLocalDateFromMillis
import com.example.tasky.agenda.domain.model.AgendaListItem
import com.example.tasky.db.TaskyDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AgendaDataSourceImpl(private val db: TaskyDatabase) : AgendaDataSource {

    override suspend fun getAllAgendaItemsByDay(dayFormatted: String): MutableList<AgendaListItem> {
        return withContext(Dispatchers.IO) {
            val agendaList = mutableListOf<AgendaListItem>()

            db.transaction {
                val events = db.eventEntityQueries.getAllEventsForToday(dayFormatted).executeAsList().map {
                    it.toEvent()
                }
                val reminders = db.reminderEntityQueries.getAllRemindersForToday(dayFormatted).executeAsList().map {
                    it.toReminder()
                }
                val tasks = db.taskEntityQueries.getAllTasksForToday(dayFormatted).executeAsList().map {
                    it.toTask()
                }

                agendaList.apply {
                    addAll(events)
                    addAll(tasks)
                    addAll(reminders)

                    sortBy { it.time }
                }

            }

            agendaList
        }
    }

    override suspend fun insertOrReplaceAgendaItems(agenda: AgendaDTO) {
        withContext(Dispatchers.IO) {
            db.transaction {
                agenda.events.forEach {
                    db.eventEntityQueries.insertOrReplaceEvent(
                        it.id,
                        it.title,
                        it.description,
                        it.from,
                        it.to,
                        it.remindAt,
                        it.host,
                        it.isUserEventCreator,
                        it.attendees,
                        it.photos,
                        it.from.getFormattedLocalDateFromMillis(),
                        emptyList(),
                        null
                    )
                }
                agenda.tasks.forEach {
                    db.taskEntityQueries.insertOrReplaceTask(
                        it.id,
                        it.title,
                        it.description,
                        it.time,
                        it.remindAt,
                        it.isDone,
                        it.time.getFormattedLocalDateFromMillis(),
                        null
                    )
                }
                agenda.reminders.forEach {
                    db.reminderEntityQueries.insertOrReplaceReminder(
                        it.id,
                        it.title,
                        it.description,
                        it.time,
                        it.remindAt,
                        it.time.getFormattedLocalDateFromMillis(),
                        null
                    )
                }
            }
        }
    }

    override suspend fun clearDatabase() {
        withContext(Dispatchers.IO) {
            db.transaction {
                db.eventEntityQueries.deleteAll()
                db.taskEntityQueries.deleteAll()
                db.reminderEntityQueries.deleteAll()
            }
        }
    }
}