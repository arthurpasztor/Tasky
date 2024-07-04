package com.example.tasky.agenda.data

import com.example.tasky.BuildConfig
import com.example.tasky.agenda.data.db.DeleteAgendaItemDataSource
import com.example.tasky.agenda.data.db.TaskDataSource
import com.example.tasky.agenda.data.dto.TaskDTO
import com.example.tasky.agenda.data.dto.toTask
import com.example.tasky.agenda.data.dto.toTaskDTO
import com.example.tasky.agenda.domain.NetworkConnectivityMonitor
import com.example.tasky.agenda.domain.TaskRepository
import com.example.tasky.agenda.domain.model.AgendaListItem.Task
import com.example.tasky.agenda.domain.model.OfflineStatus
import com.example.tasky.core.data.executeRequest
import com.example.tasky.core.domain.DataError
import com.example.tasky.core.domain.EmptyResult
import com.example.tasky.core.domain.Result
import com.example.tasky.migrations.TaskEntity
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.http.HttpMethod
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class TaskRepositoryImpl(
    private val client: HttpClient,
    private val localTaskDataSource: TaskDataSource,
    private val localDeleteItemDataSource: DeleteAgendaItemDataSource,
    private val applicationScope: CoroutineScope,
    private val networkMonitor: NetworkConnectivityMonitor
) : TaskRepository {

    private val taskUrl = "${BuildConfig.BASE_URL}/task"

    override suspend fun createTask(task: Task): EmptyResult<DataError> {
        return if (networkMonitor.isNetworkAvailable()) {
            val taskDTO = task.toTaskDTO()

            client.executeRequest<TaskDTO, Unit>(
                httpMethod = HttpMethod.Post,
                url = taskUrl,
                payload = taskDTO,
                tag = TAG
            ) {
                applicationScope.launch {
                    localTaskDataSource.insertOrReplaceTask(taskDTO)
                }
                Result.Success(Unit)
            }
        } else {
            applicationScope.launch {
                localTaskDataSource.insertOrReplaceTask(task.toTaskDTO(), OfflineStatus.CREATED)
            }.join()

            Result.Success(Unit)
        }
    }

    override suspend fun updateTask(task: Task): EmptyResult<DataError> {
        return if (networkMonitor.isNetworkAvailable()) {
            val taskDTO = task.toTaskDTO()

            client.executeRequest<TaskDTO, Unit>(
                httpMethod = HttpMethod.Put,
                url = taskUrl,
                payload = taskDTO,
                tag = TAG
            ) {
                applicationScope.launch {
                    localTaskDataSource.insertOrReplaceTask(taskDTO)
                }.join()
                Result.Success(Unit)
            }
        } else {
            applicationScope.launch {
                val taskEntity = localTaskDataSource.getTaskById(task.id)
                val appendedOfflineStatus = if (taskEntity.isOfflineCreated()) {
                    OfflineStatus.CREATED
                } else {
                    OfflineStatus.UPDATED
                }
                localTaskDataSource.insertOrReplaceTask(task.toTaskDTO(), appendedOfflineStatus)
            }.join()

            Result.Success(Unit)
        }
    }
    override suspend fun deleteTask(taskId: String): EmptyResult<DataError> {
        return if (networkMonitor.isNetworkAvailable()) {
            return client.executeRequest<Unit, Unit>(
                httpMethod = HttpMethod.Delete,
                url = taskUrl,
                queryParams = Pair(QUERY_PARAM_KEY_ID, taskId),
                tag = TAG
            ) {
                applicationScope.launch {
                    localTaskDataSource.deleteTask(taskId)
                }.join()
                Result.Success(Unit)
            }
        } else {
            applicationScope.launch {
                localDeleteItemDataSource.insertOrReplaceTaskId(taskId)
            }.join()

            Result.Success(Unit)
        }
    }

    override suspend fun getTaskDetails(taskId: String): Result<Task, DataError> {
        return if (networkMonitor.isNetworkAvailable()) {
            val result = client.executeRequest<Unit, TaskDTO>(
                httpMethod = HttpMethod.Get,
                url = taskUrl,
                queryParams = Pair(QUERY_PARAM_KEY_ID, taskId),
                tag = TAG
            ) {
                Result.Success(it.body())
            }

            when (result) {
                is Result.Success -> {
                    applicationScope.launch {
                        localTaskDataSource.insertOrReplaceTask(result.data)
                    }.join()
                    Result.Success(result.data.toTask())
                }

                is Result.Error -> Result.Error(result.error)
            }
        } else {
            val taskEntity = localTaskDataSource.getTaskById(taskId)
            if (taskEntity != null) {
                Result.Success(taskEntity.toTask())
            } else {
                Result.Error(DataError.LocalError.NOT_FOUND)
            }
        }
    }

    private fun TaskEntity?.isOfflineCreated() = this?.offlineStatus == OfflineStatus.CREATED

    companion object {
        private const val TAG = "TaskRepository"

        private const val QUERY_PARAM_KEY_ID = "taskId"
    }
}