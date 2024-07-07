package com.example.tasky.agenda.data

import com.example.tasky.BuildConfig
import com.example.tasky.agenda.data.db.DeleteAgendaItemDataSource
import com.example.tasky.agenda.data.db.ReminderDataSource
import com.example.tasky.agenda.data.dto.ReminderDTO
import com.example.tasky.agenda.data.dto.toReminder
import com.example.tasky.agenda.data.dto.toReminderDTO
import com.example.tasky.agenda.domain.NetworkConnectivityMonitor
import com.example.tasky.agenda.domain.ReminderRepository
import com.example.tasky.agenda.domain.model.AgendaListItem
import com.example.tasky.agenda.domain.model.OfflineStatus
import com.example.tasky.core.data.Preferences
import com.example.tasky.core.data.executeRequest
import com.example.tasky.core.domain.DataError
import com.example.tasky.core.domain.EmptyResult
import com.example.tasky.core.domain.Result
import com.example.tasky.migrations.ReminderEntity
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.http.HttpMethod
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class ReminderRepositoryImpl(
    private val client: HttpClient,
    private val localReminderDataSource: ReminderDataSource,
    private val localDeleteItemDataSource: DeleteAgendaItemDataSource,
    private val applicationScope: CoroutineScope,
    private val networkMonitor: NetworkConnectivityMonitor,
    private val prefs: Preferences
) : ReminderRepository {

    private val reminderUrl = "${BuildConfig.BASE_URL}/reminder"

    override suspend fun createReminder(reminder: AgendaListItem.Reminder): EmptyResult<DataError> {
        return if (networkMonitor.isNetworkAvailable()) {
            val reminderDTO = reminder.toReminderDTO()

            val result = client.executeRequest<ReminderDTO, Unit>(
                httpMethod = HttpMethod.Post,
                url = reminderUrl,
                payload = reminderDTO,
                tag = TAG
            ) {
                Result.Success(Unit)
            }

            when (result) {
                is Result.Success -> {
                    applicationScope.launch {
                        localReminderDataSource.insertOrReplaceReminder(reminderDTO)
                    }.join()
                    Result.Success(Unit)
                }

                is Result.Error -> result
            }
        } else {
            applicationScope.launch {
                localReminderDataSource.insertOrReplaceReminder(reminder.toReminderDTO(), OfflineStatus.CREATED)
            }.join()

            prefs.setOfflineActivity(true)

            Result.Success(Unit)
        }
    }

    override suspend fun updateReminder(reminder: AgendaListItem.Reminder): EmptyResult<DataError> {
        return if (networkMonitor.isNetworkAvailable()) {
            val reminderDTO = reminder.toReminderDTO()

            val result = client.executeRequest<ReminderDTO, Unit>(
                httpMethod = HttpMethod.Put,
                url = reminderUrl,
                payload = reminderDTO,
                tag = TAG
            ) {
                Result.Success(Unit)
            }

            when (result) {
                is Result.Success -> {
                    applicationScope.launch {
                        localReminderDataSource.insertOrReplaceReminder(reminderDTO)
                    }.join()
                    Result.Success(Unit)
                }

                is Result.Error -> result
            }
        } else {
            applicationScope.launch {
                val reminderEntity = localReminderDataSource.getReminderById(reminder.id)
                val appendedOfflineStatus = if (reminderEntity.isOfflineCreated()) {
                    OfflineStatus.CREATED
                } else {
                    OfflineStatus.UPDATED
                }
                localReminderDataSource.insertOrReplaceReminder(reminder.toReminderDTO(), appendedOfflineStatus)
            }.join()

            prefs.setOfflineActivity(true)

            Result.Success(Unit)
        }
    }
    override suspend fun deleteReminder(reminderId: String): EmptyResult<DataError> {
        return if (networkMonitor.isNetworkAvailable()) {
            val result = client.executeRequest<Unit, Unit>(
                httpMethod = HttpMethod.Delete,
                url = reminderUrl,
                queryParams = Pair(QUERY_PARAM_KEY_ID, reminderId),
                tag = TAG
            ) {
                Result.Success(Unit)
            }

            when (result) {
                is Result.Success -> {
                    applicationScope.launch {
                        localReminderDataSource.deleteReminder(reminderId)
                    }.join()
                    Result.Success(Unit)
                }

                is Result.Error -> result
            }
        } else {
            applicationScope.launch {
                val reminderEntity = localReminderDataSource.getReminderById(reminderId)
                if (!reminderEntity.isOfflineCreated()) {
                    localDeleteItemDataSource.insertOrReplaceReminderId(reminderId)
                }
                localReminderDataSource.deleteReminder(reminderId)
            }.join()

            prefs.setOfflineActivity(true)

            Result.Success(Unit)
        }
    }

    override suspend fun getReminderDetails(reminderId: String): Result<AgendaListItem.Reminder, DataError> {
        return if (networkMonitor.isNetworkAvailable()) {
            val result = client.executeRequest<Unit, ReminderDTO>(
                httpMethod = HttpMethod.Get,
                url = reminderUrl,
                queryParams = Pair(QUERY_PARAM_KEY_ID, reminderId),
                tag = TAG
            ) {
                Result.Success(it.body())
            }

            when (result) {
                is Result.Success -> {
                    applicationScope.launch {
                        localReminderDataSource.insertOrReplaceReminder(result.data)
                    }.join()
                    Result.Success(result.data.toReminder())
                }

                is Result.Error -> result
            }
        } else {
            val reminderEntity = localReminderDataSource.getReminderById(reminderId)
            if (reminderEntity != null) {
                Result.Success(reminderEntity.toReminder())
            } else {
                Result.Error(DataError.LocalError.NOT_FOUND)
            }
        }
    }

    private fun ReminderEntity?.isOfflineCreated() = this?.offlineStatus == OfflineStatus.CREATED

    companion object {
        private const val TAG = "ReminderRepository"

        private const val QUERY_PARAM_KEY_ID = "reminderId"
    }
}