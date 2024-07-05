package com.example.tasky.agenda.presentation.workmanager

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.tasky.agenda.data.AgendaRepositoryImpl
import com.example.tasky.agenda.domain.AgendaRepository
import com.example.tasky.agenda.domain.model.Agenda
import com.example.tasky.agenda.presentation.notification.NotificationHandler
import com.example.tasky.agenda.presentation.notification.NotificationHandlerImpl
import com.example.tasky.core.domain.onError
import com.example.tasky.core.domain.onSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent.inject
import java.time.LocalDateTime

class PeriodicFullSyncWorker(
    private val context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params), NotificationHandler by NotificationHandlerImpl() {

    private val agendaRepo: AgendaRepository by inject(AgendaRepositoryImpl::class.java)
    private val workManager: WorkManager by inject(WorkManager::class.java)

    override suspend fun doWork(): Result {
        withContext(Dispatchers.IO) {
            agendaRepo.syncFullAgenda()
                .onSuccess { agenda ->
                    scheduleNotificationForFutureAgendaItems(agenda)

                    return@withContext Result.success()
                }
                .onError {
                    return@withContext Result.failure()
                }
        }

        return Result.success()
    }

    private fun scheduleNotificationForFutureAgendaItems(agenda: Agenda) {
        val now = LocalDateTime.now()

        agenda.items.forEach {
            if (it.time.isAfter(now)) {
                workManager.scheduleNotification(it)
            }
        }
    }
}
