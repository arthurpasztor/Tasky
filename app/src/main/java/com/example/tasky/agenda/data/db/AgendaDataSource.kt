package com.example.tasky.agenda.data.db

import com.example.tasky.agenda.data.dto.AgendaDTO

interface AgendaDataSource {

    suspend fun insertOrReplaceAgendaItems(agenda: AgendaDTO)

    suspend fun clearDatabase()
}