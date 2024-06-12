package com.example.tasky.agenda.domain

import com.example.tasky.agenda.domain.model.Agenda
import com.example.tasky.core.domain.DataError
import com.example.tasky.core.domain.Result

interface AgendaRepository {
    suspend fun getDailyAgenda(time: Long): Result<Agenda, DataError>
}