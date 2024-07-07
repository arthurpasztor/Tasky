package com.example.tasky.agenda.data

import com.example.tasky.BuildConfig
import com.example.tasky.agenda.data.db.AgendaDataSource
import com.example.tasky.agenda.data.db.DeleteAgendaItemDataSource
import com.example.tasky.agenda.data.db.EventDataSource
import com.example.tasky.agenda.data.db.ReminderDataSource
import com.example.tasky.agenda.data.db.TaskDataSource
import com.example.tasky.agenda.data.dto.AgendaDTO
import com.example.tasky.agenda.data.dto.DeleteAgendaItemIdsDTO
import com.example.tasky.agenda.data.dto.toAgenda
import com.example.tasky.agenda.data.dto.toEvent
import com.example.tasky.agenda.data.dto.toEventUpdate
import com.example.tasky.agenda.data.dto.toReminder
import com.example.tasky.agenda.data.dto.toTask
import com.example.tasky.agenda.domain.AgendaRepository
import com.example.tasky.agenda.domain.EventRepository
import com.example.tasky.agenda.domain.NetworkConnectivityMonitor
import com.example.tasky.agenda.domain.ReminderRepository
import com.example.tasky.agenda.domain.TaskRepository
import com.example.tasky.agenda.domain.getFormattedLocalDateFromMillis
import com.example.tasky.agenda.domain.model.Agenda
import com.example.tasky.agenda.domain.model.OfflineStatus
import com.example.tasky.core.data.Preferences
import com.example.tasky.core.data.executeRequest
import com.example.tasky.core.domain.DataError
import com.example.tasky.core.domain.EmptyResult
import com.example.tasky.core.domain.Result
import com.example.tasky.migrations.DeleteReminderIdEntity
import com.example.tasky.migrations.DeletedEventIdEntity
import com.example.tasky.migrations.DeletedTaskIdEntity
import com.example.tasky.migrations.EventEntity
import com.example.tasky.migrations.ReminderEntity
import com.example.tasky.migrations.TaskEntity
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.http.HttpMethod
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class AgendaRepositoryImpl(
    private val client: HttpClient,
    private val eventRepo: EventRepository,
    private val taskRepo: TaskRepository,
    private val reminderRepo: ReminderRepository,
    private val localAgendaDataSource: AgendaDataSource,
    private val localEventDataSource: EventDataSource,
    private val localTaskDataSource: TaskDataSource,
    private val localReminderDataSource: ReminderDataSource,
    private val localDeleteItemDataSource: DeleteAgendaItemDataSource,
    prefs: Preferences,
    private val applicationScope: CoroutineScope,
    private val networkMonitor: NetworkConnectivityMonitor,
) : AgendaRepository {

    private val agendaUrl = "${BuildConfig.BASE_URL}/agenda"
    private val fullAgendaUrl = "${BuildConfig.BASE_URL}/fullAgenda"
    private val syncOfflineDeletedIdsUrl = "${BuildConfig.BASE_URL}/syncAgenda"

    private val currentUserId = prefs.getEncryptedString(Preferences.KEY_USER_ID, "")

    override suspend fun getDailyAgenda(time: Long): Result<Agenda, DataError> {
        return if (networkMonitor.isNetworkAvailable()) {
            val result: Result<AgendaDTO, DataError> = client.executeRequest<Unit, AgendaDTO>(
                httpMethod = HttpMethod.Get,
                url = agendaUrl,
                queryParams = Pair(QUERY_PARAM_KEY_TIME, time),
                tag = TAG
            ) {
                Result.Success(it.body())
            }

            return when (result) {
                is Result.Success -> {
                    val agendaDTO = result.data

                    applicationScope.launch {
                        localAgendaDataSource.insertOrReplaceAgendaItems(agendaDTO)
                    }.join()

                    Result.Success(agendaDTO.toAgenda())
                }

                is Result.Error -> result
            }
        } else {
            val todayFormatted = time.getFormattedLocalDateFromMillis()
            val agendaItemsList = localAgendaDataSource.getAllAgendaItemsByDay(todayFormatted, currentUserId)

            Result.Success(Agenda(agendaItemsList))
        }
    }

    override suspend fun syncFullAgenda(): Result<Agenda, DataError> {
        return if (networkMonitor.isNetworkAvailable()) {
            val result: Result<AgendaDTO, DataError> = client.executeRequest<Unit, AgendaDTO>(
                httpMethod = HttpMethod.Get,
                url = fullAgendaUrl,
                tag = TAG
            ) {
                Result.Success(it.body())
            }
            when (result) {
                is Result.Success -> {
                    val agendaDTO = result.data

                    applicationScope.launch {
                        localAgendaDataSource.insertOrReplaceAgendaItems(agendaDTO)
                    }.join()

                    Result.Success(agendaDTO.toAgenda())
                }

                is Result.Error -> result
            }
        } else {
            Result.Error(DataError.LocalError.NO_INTERNET_CONNECTION)
        }
    }

    override suspend fun syncOfflineChanges(currentUserId: String): Result<Any, DataError> {
        return if (networkMonitor.isNetworkAvailable()) {

            // First step, get all DB items that are destined to be synchronised
            var createdEvents = emptyList<EventEntity>()
            var updatedEvents = emptyList<EventEntity>()

            var createdTasks = emptyList<TaskEntity>()
            var updatedTasks = emptyList<TaskEntity>()

            var createdReminders = emptyList<ReminderEntity>()
            var updatedReminders = emptyList<ReminderEntity>()

            applicationScope.launch {
                createdEvents = localEventDataSource.getAllOfflineEvents(currentUserId, OfflineStatus.CREATED)
                updatedEvents = localEventDataSource.getAllOfflineEvents(currentUserId, OfflineStatus.UPDATED)

                createdTasks = localTaskDataSource.getAllOfflineTasks(currentUserId, OfflineStatus.CREATED)
                updatedTasks = localTaskDataSource.getAllOfflineTasks(currentUserId, OfflineStatus.UPDATED)

                createdReminders = localReminderDataSource.getAllOfflineReminders(currentUserId, OfflineStatus.CREATED)
                updatedReminders = localReminderDataSource.getAllOfflineReminders(currentUserId, OfflineStatus.UPDATED)
            }.join()

            // Second step, sync items created / updated / deleted offline
            var resultsList = listOf<Result<Any, DataError>>()
            applicationScope.launch {
                val deferredJobs = mutableListOf<Deferred<Result<Any, DataError>>>()

                createdEvents.forEach {
                    deferredJobs.add(async { eventRepo.createEvent(it.toEvent(), emptyList()) })
                }
                updatedEvents.forEach {
                    deferredJobs.add(async { eventRepo.updateEvent(it.toEventUpdate(currentUserId), emptyList()) })
                }

                createdTasks.forEach {
                    deferredJobs.add(async { taskRepo.createTask(it.toTask()) })
                }
                updatedTasks.forEach {
                    deferredJobs.add(async { taskRepo.updateTask(it.toTask()) })
                }

                createdReminders.forEach {
                    deferredJobs.add(async { reminderRepo.createReminder(it.toReminder()) })
                }
                updatedReminders.forEach {
                    deferredJobs.add(async { reminderRepo.updateReminder(it.toReminder()) })
                }

                deferredJobs.add(async { syncOfflineDeletedItems() })

                resultsList = deferredJobs.map { it.await() }
            }.join()

            resultsList.find { result -> result is Result.Error } ?: run {
                Result.Success(Unit)
            }
        } else {
            Result.Error(DataError.LocalError.NO_INTERNET_CONNECTION)
        }
    }

    private suspend fun syncOfflineDeletedItems(): EmptyResult<DataError> {
        return if (networkMonitor.isNetworkAvailable()) {
            var deletedEventIds = emptyList<DeletedEventIdEntity>()
            var deletedTaskIds = emptyList<DeletedTaskIdEntity>()
            var deletedReminderIds = emptyList<DeleteReminderIdEntity>()
            applicationScope.launch {
                deletedEventIds = localDeleteItemDataSource.getAllEventIds(currentUserId)
                deletedTaskIds = localDeleteItemDataSource.getAllTaskIds(currentUserId)
                deletedReminderIds = localDeleteItemDataSource.getAllReminderIds(currentUserId)
            }.join()

            val payload = DeleteAgendaItemIdsDTO(
                deletedEventIds = deletedEventIds.map { it.deletedEventId },
                deletedTaskIds = deletedTaskIds.map { it.deletedTaskId },
                deletedReminderIds = deletedReminderIds.map { it.deleteReminderId }
            )

            if (payload.isEmpty()) {
                Result.Success(Unit)
            } else {
                val result: EmptyResult<DataError> = client.executeRequest<DeleteAgendaItemIdsDTO, Unit>(
                    httpMethod = HttpMethod.Post,
                    url = syncOfflineDeletedIdsUrl,
                    payload = payload,
                    tag = TAG
                ) {
                    Result.Success(Unit)
                }

                when (result) {
                    is Result.Success -> {
                        applicationScope.launch {
                            localDeleteItemDataSource.clearAll(currentUserId)
                        }.join()

                        Result.Success(Unit)
                    }

                    is Result.Error -> result
                }
            }
        } else {
            Result.Error(DataError.LocalError.NO_INTERNET_CONNECTION)
        }
    }

    companion object {
        private const val TAG = "AgendaRepo http db"

        private const val QUERY_PARAM_KEY_TIME = "time"
    }
}