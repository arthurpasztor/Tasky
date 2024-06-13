package com.example.tasky.agenda.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tasky.R
import com.example.tasky.auth.presentation.showToast
import com.example.tasky.destinations.TextEditorRootDestination
import com.example.tasky.agenda.domain.AgendaItemType
import com.example.tasky.agenda.domain.DetailInteractionMode
import com.example.tasky.agenda.domain.DetailItemType
import com.example.tasky.agenda.domain.ReminderType
import com.example.tasky.agenda.domain.formatDetailDate
import com.example.tasky.agenda.domain.formatDetailTime
import com.example.tasky.ui.theme.BackgroundBlack
import com.example.tasky.ui.theme.BackgroundWhite
import com.example.tasky.ui.theme.ReminderBorderGray
import com.example.tasky.ui.theme.ReminderGray
import com.example.tasky.ui.theme.TaskyGreen
import com.example.tasky.ui.theme.VeryLightGray
import com.example.tasky.ui.theme.detailDescriptionStyle
import com.example.tasky.ui.theme.detailTitleStyle
import com.example.tasky.ui.theme.detailTypeStyle
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
fun TaskReminderDetailRoot(
    navigator: DestinationsNavigator,
    resultRecipient: ResultRecipient<TextEditorRootDestination, TextEditorResponse>,
    type: AgendaItemType,
    mode: DetailInteractionMode,
    itemId: String? = null
) {

    val TAG = "TaskReminderDetailScreen"

    val context = LocalContext.current
    val viewModel: AgendaDetailsViewModel = getViewModel(parameters = { parametersOf(type, mode, itemId) })
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(true) {
        viewModel.navChannel.collect { destination ->
            when (destination) {
                TaskReminderVMAction.OpenTitleEditor -> {
                    navigator.navigate(
                        TextEditorRootDestination(
                            text = state.title,
                            type = DetailItemType.TITLE,
                        )
                    )
                }

                TaskReminderVMAction.OpenDescriptionEditor -> {
                    navigator.navigate(
                        TextEditorRootDestination(
                            text = state.description,
                            type = DetailItemType.DESCRIPTION,
                        )
                    )
                }

                TaskReminderVMAction.CreateTaskSuccess -> {
                    context.showToast(R.string.success_task_created)
                    navigator.popBackStack()
                }

                is TaskReminderVMAction.CreateTaskError -> context.showToast(destination.error, TAG)

                TaskReminderVMAction.CreateReminderSuccess -> {
                    context.showToast(R.string.success_reminder_created)
                    navigator.popBackStack()
                }

                is TaskReminderVMAction.CreateReminderError -> context.showToast(destination.error, TAG)
                is TaskReminderVMAction.LoadReminderError -> context.showToast(destination.error, TAG)
                is TaskReminderVMAction.LoadTaskError -> context.showToast(destination.error, TAG)
            }
        }
    }

    resultRecipient.onNavResult { result ->
        if (result is NavResult.Value) {
            when (result.value.type) {
                DetailItemType.TITLE -> viewModel.onAction(TaskReminderAction.UpdateTitle(result.value.newText))
                DetailItemType.DESCRIPTION -> viewModel.onAction(TaskReminderAction.UpdateDescription(result.value.newText))
            }
        }
    }

    TaskReminderDetailScreen(
        state = state,
        onAction = viewModel::onAction
    ) {
        navigator.popBackStack()
    }
    if (state.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}

@Preview
@Composable
private fun TaskReminderDetailScreen(
    state: AgendaItemState = AgendaItemState(),
    onAction: (TaskReminderAction) -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    val cornerRadius = dimensionResource(R.dimen.radius_30)

    val dateDialogState = rememberMaterialDialogState()
    val timeDialogState = rememberMaterialDialogState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundBlack)
    ) {
        AgendaItemDetailHeader(
            agendaItemType = state.agendaItemType,
            interactionMode = state.interactionMode,
            headerDate = state.date,
            onNavigateBack = { onNavigateBack() },
            onSwitchToEditMode = { onAction(TaskReminderAction.SwitchToEditMode) },
            onSave = {
                onAction(
                    if (state.agendaItemType == AgendaItemType.TASK)
                        TaskReminderAction.SaveTask
                    else
                        TaskReminderAction.SaveReminder
                )
            })
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
                        .background(if (state.agendaItemType == AgendaItemType.TASK) TaskyGreen else ReminderGray)
                        .border(
                            BorderStroke(
                                1.dp,
                                if (state.agendaItemType == AgendaItemType.TASK) TaskyGreen else ReminderBorderGray
                            )
                        )
                        .align(Alignment.CenterVertically),
                )
                Text(
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .align(Alignment.CenterVertically),
                    text = stringResource(id = if (state.agendaItemType == AgendaItemType.TASK) R.string.task else R.string.reminder),
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
                        .weight(1f)
                        .padding(start = 10.dp),
                    text = state.title,
                    style = detailTitleStyle,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                ArrowEditButton {
                    onAction.invoke(TaskReminderAction.OpenTitleEditor)
                }
            }

            HorizontalDivider(color = VeryLightGray, thickness = 1.dp)

            Row(modifier = Modifier.padding(vertical = 8.dp)) {
                Text(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .weight(1f),
                    text = state.description,
                    style = detailDescriptionStyle,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                ArrowEditButton {
                    onAction.invoke(TaskReminderAction.OpenDescriptionEditor)
                }
            }

            HorizontalDivider(color = VeryLightGray, thickness = 1.dp)

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

            HorizontalDivider(color = VeryLightGray, thickness = 1.dp)

            ReminderSelector(
                modifier = Modifier.padding(vertical = 20.dp),
                state = state,
                onAction = onAction
            )

            HorizontalDivider(color = VeryLightGray, thickness = 1.dp)
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
            onAction(TaskReminderAction.UpdateDate(it))
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
            onAction(TaskReminderAction.UpdateTime(it))
        }
    }
}

sealed class TaskReminderAction {
    data object OpenTitleEditor : TaskReminderAction()
    data object OpenDescriptionEditor : TaskReminderAction()
    data object SwitchToEditMode : TaskReminderAction()
    class UpdateTitle(val newTitle: String) : TaskReminderAction()
    class UpdateDescription(val newDescription: String) : TaskReminderAction()
    class UpdateDate(val newDate: LocalDate) : TaskReminderAction()
    class UpdateTime(val newTime: LocalTime) : TaskReminderAction()
    class UpdateReminder(val newReminder: ReminderType) : TaskReminderAction()
    data object SaveTask : TaskReminderAction()
    data object SaveReminder : TaskReminderAction()
}