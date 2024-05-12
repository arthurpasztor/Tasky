package com.example.tasky.main.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tasky.R
import com.example.tasky.main.domain.formatToHeader
import com.example.tasky.ui.theme.BackgroundBlack
import com.example.tasky.ui.theme.BackgroundWhite
import com.example.tasky.ui.theme.headerStyle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf
import java.time.LocalDate

@Destination
@Composable
fun TaskDetailRoot(navigator: DestinationsNavigator, mode: DetailInteractionMode) {

    val TAG = "TaskDetailScreen"

    val viewModel: TaskViewModel = getViewModel(parameters = { parametersOf(mode) })
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(true) {
        viewModel.navChannel.collect { destination ->
            when (destination) {
                TaskVMAction.NavigateBack -> navigator.popBackStack()
            }
        }
    }

    TaskDetailScreen(
        state = state,
        onAction = viewModel::onAction
    )
    if (state.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}

@Preview
@Composable
private fun TaskDetailScreen(
    state: TaskState = TaskState(),
    onAction: (TaskAction) -> Unit = {}
) {
    val cornerRadius = dimensionResource(R.dimen.radius_30)
    val headerPadding = dimensionResource(R.dimen.padding_20)

    val headerText = when (state.interactionMode) {
        DetailInteractionMode.CREATE -> LocalDate.now().formatToHeader()
        DetailInteractionMode.EDIT -> stringResource(id = R.string.edit_task).uppercase()
        DetailInteractionMode.VIEW -> LocalDate.now().formatToHeader() //TODO change to the current task's date
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundBlack)
    ) {
        Row {
            CloseButton { onAction.invoke(TaskAction.NavigateBack) }
            Spacer(Modifier.weight(1f))
            Text(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(end = headerPadding),
                text = headerText,
                style = headerStyle
            )
            Spacer(Modifier.weight(1f))
            when (state.interactionMode) {
                DetailInteractionMode.CREATE, DetailInteractionMode.EDIT -> {
                    Text(
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(end = headerPadding),
                        text = stringResource(id = R.string.save),
                        style = headerStyle
                    )
                }

                DetailInteractionMode.VIEW -> {
                    IconButton(
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .size(60.dp)
                            .padding(8.dp),
                        onClick = {
                            onAction(TaskAction.SwitchToEditMode)
                        }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "create, edit",
                            tint = Color.White,
                        )
                    }
                }
            }

        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(cornerRadius, cornerRadius, 0.dp, 0.dp))
                .background(BackgroundWhite)
        ) {

        }
    }
}

sealed class TaskAction {
    data object NavigateBack : TaskAction()
    data object SwitchToEditMode : TaskAction()
}