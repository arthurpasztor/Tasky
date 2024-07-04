package com.example.tasky.agenda.data.db

import com.example.tasky.agenda.data.dto.AgendaDTO
import com.example.tasky.agenda.domain.model.Agenda

interface AgendaDataSource {

    suspend fun getAllAgendaItemsByDay(dayFormatted: String): Agenda

    suspend fun insertOrReplaceAgendaItems(agenda: AgendaDTO)

    suspend fun clearDatabase()
}