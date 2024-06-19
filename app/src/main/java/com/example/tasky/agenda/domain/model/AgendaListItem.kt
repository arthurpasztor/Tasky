package com.example.tasky.agenda.domain.model

import com.example.tasky.agenda.domain.formatAgendaDateTime
import java.time.LocalDateTime
import java.util.UUID

sealed class AgendaListItem {

    open val id: String = UUID.randomUUID().toString()
    open val time: LocalDateTime = LocalDateTime.now()

    open fun getFormattedTime() : String = time.formatAgendaDateTime()

    fun isAfterNow() = time.isAfter(LocalDateTime.now())

    fun isBeforeNow() = time.isBefore(LocalDateTime.now())

    data class Event(
        override val id: String,
        val title: String,
        val description: String,
        override val time: LocalDateTime,
        val to: LocalDateTime,
        val remindAt: LocalDateTime,
        val host: String,
        val isUserEventCreator: Boolean,
        val attendees: List<Attendee>,
        val photos: List<Photo>

    ): AgendaListItem() {

        override fun getFormattedTime() = "${time.formatAgendaDateTime()} - ${to.formatAgendaDateTime()}"

        companion object {
            fun getSampleEvent() = Event(
                id = UUID.randomUUID().toString(),
                title = "Sample Event",
                description = "This is a sample event",
                time = LocalDateTime.now(),
                to = LocalDateTime.now(),
                remindAt = LocalDateTime.now().minusMinutes(30),
                host = UUID.randomUUID().toString(),
                isUserEventCreator = true,
                attendees = listOf(
                    Attendee.getSampleAttendeeGoing(),
                    Attendee.getSampleAttendeeNotGoing()
                ),
                photos = listOf(
                    Photo.getSamplePhoto()
                )
            )
        }
    }

    data class Task(
        override val id: String,
        val title: String,
        val description: String,
        override val time: LocalDateTime,
        val remindAt: LocalDateTime = LocalDateTime.now(),
        val isDone: Boolean
    ): AgendaListItem() {

        companion object {
            fun getSampleTask() = Task(
                id = UUID.randomUUID().toString(),
                title = "Sample Task",
                description = "This is a sample task",
                time = LocalDateTime.now(),
                remindAt = LocalDateTime.now().minusMinutes(30),
                isDone = true
            )
        }
    }

    data class Reminder(
        override val id: String,
        val title: String,
        val description: String,
        override val time: LocalDateTime,
        val remindAt: LocalDateTime = LocalDateTime.now()
    ): AgendaListItem() {

        companion object {
            fun getSampleReminder() = Reminder(
                id = UUID.randomUUID().toString(),
                title = "Sample Reminder",
                description = "This is a sample reminder",
                time = LocalDateTime.now(),
                remindAt = LocalDateTime.now().minusMinutes(30)
            )
        }
    }
}

data class Attendee(
    val email: String,
    val fullName: String,
    val userId: String,
    val eventId: String,
    val isGoing: Boolean,
    val remindAt: LocalDateTime
) {
    companion object {
        fun getSampleAttendeeGoing() = Attendee(
            email = "john.c.calhoun@examplepetstore.com",
            fullName = "Kevin Malone",
            userId = "user123",
            eventId = "event123",
            isGoing = true,
            remindAt = LocalDateTime.now()
        )

        fun getSampleAttendeeNotGoing() = Attendee(
            email = "dwight.c.calhoun@examplepetstore.com",
            fullName = "Dwight Schrute",
            userId = "user789",
            eventId = "event123",
            isGoing = false,
            remindAt = LocalDateTime.now()
        )
    }
}

data class Photo(
    val key: String,
    val url: String
) {
    companion object {
        fun getSamplePhoto() = Photo(
            key = "key",
            url = "https://example.com/photo.jpg"
        )
    }
}