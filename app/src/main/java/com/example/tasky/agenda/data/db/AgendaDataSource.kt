package com.example.tasky.agenda.data.db

import com.example.tasky.agenda.data.dto.AgendaDTO
import com.example.tasky.agenda.domain.model.AgendaListItem

interface AgendaDataSource {

    suspend fun getAllAgendaItemsByDay(dayFormatted: String): MutableList<AgendaListItem>

    suspend fun insertOrReplaceAgendaItems(agenda: AgendaDTO)

    suspend fun clearDatabaseWithNoOfflineChanges()
}