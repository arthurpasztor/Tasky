package com.example.tasky.agenda.domain

import com.example.tasky.core.domain.Result
import com.example.tasky.core.domain.RootError

interface AgendaRepository {
    suspend fun getDailyAgenda(time: Long): Result<Agenda, RootError>
}