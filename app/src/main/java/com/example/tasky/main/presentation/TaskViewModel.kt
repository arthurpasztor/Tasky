package com.example.tasky.main.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasky.main.data.ApiRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TaskViewModel(private val repository: ApiRepository, mode: DetailInteractionMode) : ViewModel() {

    private val _state = MutableStateFlow(TaskState(interactionMode = mode))
    val state = _state.asStateFlow()

    private val _navChannel = Channel<TaskVMAction>()
    val navChannel = _navChannel.receiveAsFlow()

    fun onAction(action: TaskAction) {
        when (action) {
            TaskAction.NavigateBack -> navigateBack()
            TaskAction.SwitchToEditMode -> switchToEditMode()
        }
    }

    private fun navigateBack() {
        viewModelScope.launch {
            _navChannel.send(TaskVMAction.NavigateBack)
        }
    }

    private fun switchToEditMode() {
        viewModelScope.launch {
            _state.update { it.copy(interactionMode = DetailInteractionMode.EDIT) }
        }
    }
}

data class TaskState(
    val isLoading: Boolean = false,

    val title: String = "",
    val description: String? = null,
    val time: Long = 0,
    val remindAt: Long = 0,
    val isDone: Boolean = false,

    val interactionMode: DetailInteractionMode = DetailInteractionMode.CREATE
)

sealed class TaskVMAction {
    data object NavigateBack : TaskVMAction()
}