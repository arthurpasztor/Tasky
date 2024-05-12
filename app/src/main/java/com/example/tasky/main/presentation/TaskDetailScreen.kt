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
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tasky.R
import com.example.tasky.destinations.TextEditorRootDestination
import com.example.tasky.main.domain.DetailInteractionMode
import com.example.tasky.main.domain.DetailItemType
import com.example.tasky.main.domain.ReminderType
import com.example.tasky.main.domain.formatDetailDate
import com.example.tasky.main.domain.formatDetailTime
import com.example.tasky.main.domain.formatHeaderDate
import com.example.tasky.ui.theme.BackgroundBlack
import com.example.tasky.ui.theme.BackgroundWhite
import com.example.tasky.ui.theme.TaskyGreen
import com.example.tasky.ui.theme.VeryLightGray
import com.example.tasky.ui.theme.detailDescriptionStyle
import com.example.tasky.ui.theme.detailTitleStyle
import com.example.tasky.ui.theme.detailTypeStyle
import com.example.tasky.ui.theme.headerStyle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf
import java.time.LocalDate
import java.time.LocalTime

@Destination
@Composable
fun TaskDetailRoot(
    navigator: DestinationsNavigator,
    resultRecipient: ResultRecipient<TextEditorRootDestination, TextEditorResponse>,
    mode: DetailInteractionMode
) {

    val TAG = "TaskDetailScreen"

    val viewModel: TaskViewModel = getViewModel(parameters = { parametersOf(mode) })
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(true) {
        viewModel.navChannel.collect { destination ->
            when (destination) {
                TaskVMAction.NavigateBack -> navigator.popBackStack()
                TaskVMAction.OpenTitleEditor -> {
                    navigator.navigate(
                        TextEditorRootDestination(
                            text = state.title,
                            type = DetailItemType.TITLE,
                        )
                    )
                }

                TaskVMAction.OpenDescriptionEditor -> {
                    navigator.navigate(
                        TextEditorRootDestination(
                            text = state.description,
                            type = DetailItemType.DESCRIPTION,
                        )
                    )
                }

            }
        }
    }

    resultRecipient.onNavResult { result ->
        if (result is NavResult.Value) {
            when (result.value.type) {
                DetailItemType.TITLE -> viewModel.onAction(TaskAction.UpdateTitle(result.value.newText))
                DetailItemType.DESCRIPTION -> viewModel.onAction(TaskAction.UpdateDescription(result.value.newText))
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

    val dateDialogState = rememberMaterialDialogState()
    val timeDialogState = rememberMaterialDialogState()

    val headerText = when (state.interactionMode) {
        DetailInteractionMode.CREATE -> LocalDate.now().formatHeaderDate()
        DetailInteractionMode.EDIT -> stringResource(id = R.string.edit_task).uppercase()
        DetailInteractionMode.VIEW -> LocalDate.now().formatHeaderDate() //TODO change to the current task's date
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
                .padding(horizontal = 16.dp)
        ) {
            Row(modifier = Modifier.padding(top = 20.dp)) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10))
                        .size(18.dp)
                        .background(TaskyGreen)
                        .align(Alignment.CenterVertically),
                )
                Text(
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .align(Alignment.CenterVertically),
                    text = stringResource(id = R.string.task),
                    style = detailTypeStyle,
                )
            }

            Row(modifier = Modifier.padding(vertical = 16.dp)) {
                RadioButton(
                    modifier = Modifier
                        .size(18.dp)
                        .align(Alignment.CenterVertically),
                    selected = false,
                    onClick = { }
                )
                Text(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(start = 10.dp),
                    text = state.title,
                    style = detailTitleStyle,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.weight(1f))
                ArrowEditButton {
                    onAction.invoke(TaskAction.OpenTitleEditor)
                }
            }

            Divider(color = VeryLightGray, thickness = 1.dp)

            Row(modifier = Modifier.padding(vertical = 8.dp)) {
                Text(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    text = state.description,
                    style = detailDescriptionStyle,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.weight(1f))
                ArrowEditButton {
                    onAction.invoke(TaskAction.OpenDescriptionEditor)
                }
            }

            Divider(color = VeryLightGray, thickness = 1.dp)

            Row(modifier = Modifier.padding(vertical = 20.dp)) {
                Text(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    text = stringResource(id = R.string.at),
                    style = detailDescriptionStyle,
                    maxLines = 1
                )
                Spacer(Modifier.weight(1f))
                ClickableText(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    text = AnnotatedString(state.time.formatDetailTime()),
                    style = detailDescriptionStyle,
                    maxLines = 1,
                    onClick = {
                        timeDialogState.show()
                    }
                )
                Spacer(Modifier.weight(1f))
                ClickableText(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    text = AnnotatedString(state.date.formatDetailDate()),
                    style = detailDescriptionStyle,
                    maxLines = 1,
                    onClick = {
                        dateDialogState.show()
                    }
                )
                Spacer(Modifier.weight(1f))
            }

            Divider(color = VeryLightGray, thickness = 1.dp)

            ReminderSelector(
                modifier = Modifier.padding(vertical = 20.dp),
                state = state,
                onAction = onAction
            )

            Divider(color = VeryLightGray, thickness = 1.dp)
        }
    }

    val now = LocalDate.now()

    MaterialDialog(
        dialogState = dateDialogState,
        buttons = {
            positiveButton(text = stringResource(id = R.string.ok))
            negativeButton(text = stringResource(id = R.string.cancel))
        }
    ) {
        datepicker(
            initialDate = state.date,
            title = stringResource(id = R.string.pick_a_date),
            allowedDateValidator = {
                it.isAfter(now) || it.isEqual(now)
            }
        ) {
            onAction(TaskAction.UpdateDate(it))
        }
    }

    MaterialDialog(
        dialogState = timeDialogState,
        buttons = {
            positiveButton(text = stringResource(id = R.string.ok))
            negativeButton(text = stringResource(id = R.string.cancel))
        }
    ) {
        timepicker(
            initialTime = state.time,
            title = stringResource(id = R.string.pick_a_time)
        ) {
            onAction(TaskAction.UpdateTime(it))
        }
    }
}

sealed class TaskAction {
    data object NavigateBack : TaskAction()
    data object OpenTitleEditor : TaskAction()
    data object OpenDescriptionEditor : TaskAction()
    data object SwitchToEditMode : TaskAction()
    class UpdateTitle(val newTitle: String) : TaskAction()
    class UpdateDescription(val newDescription: String) : TaskAction()
    class UpdateDate(val newDate: LocalDate) : TaskAction()
    class UpdateTime(val newTime: LocalTime) : TaskAction()
    class UpdateReminder(val newReminder: ReminderType) : TaskAction()
}