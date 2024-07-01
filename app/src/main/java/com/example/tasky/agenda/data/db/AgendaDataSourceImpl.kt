package com.example.tasky.agenda.data.db

import com.example.tasky.agenda.data.dto.AgendaDTO
import com.example.tasky.db.TaskyDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AgendaDataSourceImpl(private val db: TaskyDatabase) : AgendaDataSource {

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
                        it.photos
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
                    )
                }
                agenda.reminders.forEach {
                    db.reminderEntityQueries.insertOrReplaceReminder(
                        it.id,
                        it.title,
                        it.description,
                        it.time,
                        it.remindAt
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