package com.example.tasky.agenda.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tasky.R
import com.example.tasky.agenda.domain.AgendaItemType
import com.example.tasky.agenda.domain.DetailItemType
import com.example.tasky.agenda.domain.ReminderType
import com.example.tasky.agenda.domain.formatDetailDate
import com.example.tasky.agenda.domain.formatDetailTime
import com.example.tasky.agenda.domain.model.Attendee
import com.example.tasky.auth.presentation.showToast
import com.example.tasky.core.presentation.ObserveAsEvents
import com.example.tasky.destinations.TextEditorRootDestination
import com.example.tasky.ui.theme.BackgroundBlack
import com.example.tasky.ui.theme.BackgroundGray
import com.example.tasky.ui.theme.BackgroundWhite
import com.example.tasky.ui.theme.EventGreen
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
fun AgendaDetailRoot(
    navigator: DestinationsNavigator,
    resultRecipient: ResultRecipient<TextEditorRootDestination, TextEditorResponse>,
    type: AgendaItemType,
    itemId: String? = null,
    editable: Boolean = true
) {

    val TAG = "TaskDetailScreen"

    val context = LocalContext.current
    val viewModel: AgendaDetailsViewModel = getViewModel(parameters = { parametersOf(type, itemId, editable) })
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.navChannel) { destination ->
        when (destination) {
            AgendaDetailVMAction.OpenTitleEditor -> {
                navigator.navigate(
                    TextEditorRootDestination(
                        text = state.title,
                        type = DetailItemType.TITLE,
                    )
                )
            }

            AgendaDetailVMAction.OpenDescriptionEditor -> {
                navigator.navigate(
                    TextEditorRootDestination(
                        text = state.description,
                        type = DetailItemType.DESCRIPTION,
                    )
                )
            }

            is AgendaDetailVMAction.CreateAgendaItemSuccess -> {
                context.showToast(
                    when (destination.itemType) {
                        AgendaItemType.EVENT -> TODO()
                        AgendaItemType.TASK -> R.string.success_task_created
                        AgendaItemType.REMINDER -> R.string.success_reminder_created
                    }
                )

                navigator.popBackStack()
            }

            is AgendaDetailVMAction.AgendaItemError -> context.showToast(destination.error, TAG)
        }
    }

    resultRecipient.onNavResult { result ->
        if (result is NavResult.Value) {
            when (result.value.type) {
                DetailItemType.TITLE -> viewModel.onAction(AgendaDetailAction.UpdateTitle(result.value.newText))
                DetailItemType.DESCRIPTION -> viewModel.onAction(AgendaDetailAction.UpdateDescription(result.value.newText))
            }
        }
    }

    AgendaDetailScreen(
        state = state,
        onAction = viewModel::onAction,
        onNavigateBack = { navigator.popBackStack() }
    )

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
private fun AgendaDetailScreenPreview() {
    AgendaDetailScreen(
        state = AgendaDetailsState(
            extras = AgendaItemDetails.EventItemDetail(
                attendeeSelection = AttendeeSelection.ALL,
                attendees = listOf(Attendee.getSampleAttendeeGoing()),
                nonAttendees = listOf(Attendee.getSampleAttendeeNotGoing())
            )
        ),
        onAction = {},
        onNavigateBack = {}
    )
}

@Composable
private fun AgendaDetailScreen(
    state: AgendaDetailsState,
    onAction: (AgendaDetailAction) -> Unit,
    onNavigateBack: () -> Unit
) {
    val cornerRadius = dimensionResource(R.dimen.radius_30)

    val dateDialogState = rememberMaterialDialogState()
    val timeDialogState = rememberMaterialDialogState()
    val dateEventEndDialogState = rememberMaterialDialogState()
    val timeEventEndDialogState = rememberMaterialDialogState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(BackgroundBlack)
    ) {
        AgendaItemDetailHeader(
            state = state,
            onNavigateBack = { onNavigateBack() },
            onSwitchToEditMode = { onAction(AgendaDetailAction.SwitchToEditMode) },
            onSave = {
                onAction(
                    when (state.agendaItemType) {
                        AgendaItemType.EVENT -> AgendaDetailAction.SaveEvent
                        AgendaItemType.TASK -> AgendaDetailAction.SaveTask
                        AgendaItemType.REMINDER -> AgendaDetailAction.SaveReminder
                    }
                )
            })
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(cornerRadius, cornerRadius, 0.dp, 0.dp))
                .background(BackgroundWhite)
        ) {
            // Label
            Row(modifier = Modifier.padding(top = 20.dp, start = 16.dp, end = 16.dp)) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10))
                        .size(18.dp)
                        .background(
                            color = when (state.agendaItemType) {
                                AgendaItemType.EVENT -> EventGreen
                                AgendaItemType.TASK -> TaskyGreen
                                AgendaItemType.REMINDER -> ReminderGray
                            }
                        )
                        .border(
                            BorderStroke(
                                width = 1.dp,
                                color = when (state.agendaItemType) {
                                    AgendaItemType.EVENT -> EventGreen
                                    AgendaItemType.TASK -> TaskyGreen
                                    AgendaItemType.REMINDER -> ReminderBorderGray
                                }
                            )
                        )
                        .align(Alignment.CenterVertically),
                )
                Text(
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .align(Alignment.CenterVertically),
                    text = stringResource(
                        id = when (state.agendaItemType) {
                            AgendaItemType.EVENT -> R.string.event
                            AgendaItemType.TASK -> R.string.task
                            AgendaItemType.REMINDER -> R.string.reminder
                        }
                    ),
                    style = detailTypeStyle,
                )
            }

            // Title
            Row(modifier = Modifier.padding(16.dp)) {
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
                    onAction.invoke(AgendaDetailAction.OpenTitleEditor)
                }
            }

            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = VeryLightGray, thickness = 1.dp)

            // Description
            Row(modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)) {
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
                    onAction.invoke(AgendaDetailAction.OpenDescriptionEditor)
                }
            }

            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = VeryLightGray, thickness = 1.dp)

            // Photos
            if (state.isEvent()) {
                if (state.isUserEventCreator() && (state.isCreateMode() || state.isEditMode())) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .border(BorderStroke(1.dp, BackgroundGray))
                            .background(BackgroundGray),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "plus",
                            tint = Color.Gray
                        )
                        Text(
                            modifier = Modifier.padding(start = 12.dp),
                            text = stringResource(id = R.string.add_photos),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )

                        Spacer(modifier = Modifier.weight(1f))
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp),
                        color = VeryLightGray,
                        thickness = 1.dp
                    )
                }
            }

            // Date #1
            Row(modifier = Modifier.padding(vertical = 20.dp, horizontal = 16.dp)) {
                Text(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    text = stringResource(
                        id = if (state.agendaItemType == AgendaItemType.EVENT) {
                            R.string.from
                        } else {
                            R.string.at
                        }
                    ),
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

            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = VeryLightGray, thickness = 1.dp)

            // Date #2
            if (state.isEvent()) {

                Row(modifier = Modifier.padding(vertical = 20.dp, horizontal = 16.dp)) {
                    Text(
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(end = 16.dp),
                        text = stringResource(id = R.string.to),
                        style = detailDescriptionStyle,
                        maxLines = 1
                    )
                    Spacer(Modifier.weight(1f))
                    ClickableText(
                        modifier = Modifier.align(Alignment.CenterVertically),
                        text = AnnotatedString(state.getEventTime().formatDetailTime()),
                        style = detailDescriptionStyle,
                        maxLines = 1,
                        onClick = {
                            timeEventEndDialogState.show()
                        }
                    )
                    Spacer(Modifier.weight(1f))
                    ClickableText(
                        modifier = Modifier.align(Alignment.CenterVertically),
                        text = AnnotatedString(state.getEventDate().formatDetailDate()),
                        style = detailDescriptionStyle,
                        maxLines = 1,
                        onClick = {
                            dateEventEndDialogState.show()
                        }
                    )
                    Spacer(Modifier.weight(1f))
                }

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = VeryLightGray,
                    thickness = 1.dp
                )
            }

            // Reminder
            ReminderSelector(
                modifier = Modifier.padding(top = 20.dp, bottom = 20.dp, start = 16.dp, end = 34.dp),
                state = state,
                onAction = onAction
            )

            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = VeryLightGray, thickness = 1.dp)

            // Attendees
            if (state.isEvent()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 26.dp, bottom = 26.dp)
                ) {
                    Text(
                        modifier = Modifier.align(Alignment.CenterVertically),
                        text = stringResource(id = R.string.visitors),
                        style = detailTitleStyle,
                        fontSize = 22.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.size(16.dp))
                    if (state.isCreateMode() || state.isEditMode()) {
                        AddAttendeeButton(
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .size(30.dp)
                        ) {
                            // TODO add visitor
                        }
                    }
                }

                AttendeeToggleToolbar(state, onAction)

                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    if (state.isAllAttendeesSelected() || state.isGoingAttendeesSelected()) {
                        AttendeeList(
                            modifier = Modifier,
                            labelRes = R.string.going,
                            list = state.getAttendees(),
                            creatorFullName = state.getCurrentUserFullNameIfEventCreator(),
                            onRemoveAttendee = {
                                onAction(AgendaDetailAction.RemoveAttendee(it))
                            }
                        )
                    }
                    if (state.isAllAttendeesSelected() || state.isNotGoingAttendeesSelected()) {
                        AttendeeList(
                            modifier = Modifier.align(Alignment.Start),
                            labelRes = R.string.not_going,
                            list = state.getNonAttendees(),
                            onRemoveAttendee = {
                                onAction(AgendaDetailAction.RemoveAttendee(it))
                            }
                        )
                    }
                }
            }
        }
    }

    //region DateTime Picker Dialogs
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
            onAction(AgendaDetailAction.UpdateDate(it))
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
            onAction(AgendaDetailAction.UpdateTime(it))
        }
    }

    MaterialDialog(
        dialogState = dateEventEndDialogState,
        buttons = {
            positiveButton(text = stringResource(id = R.string.ok))
            negativeButton(text = stringResource(id = R.string.cancel))
        }
    ) {
        datepicker(
            initialDate = state.getEventDate(),
            title = stringResource(id = R.string.pick_a_date),
            allowedDateValidator = {
                it.isAfter(state.date) || it.isEqual(state.date)
            }
        ) {
            onAction(AgendaDetailAction.UpdateEventEndDate(it))
        }
    }

    MaterialDialog(
        dialogState = timeEventEndDialogState,
        buttons = {
            positiveButton(text = stringResource(id = R.string.ok))
            negativeButton(text = stringResource(id = R.string.cancel))
        }
    ) {
        timepicker(
            initialTime = state.getEventTime(),
            title = stringResource(id = R.string.pick_a_time)
        ) {
            onAction(AgendaDetailAction.UpdateEventEndTime(it))
        }
    }
    //endregion
}

sealed interface AgendaDetailAction {
    data object OpenTitleEditor : AgendaDetailAction
    data object OpenDescriptionEditor : AgendaDetailAction
    data object SwitchToEditMode : AgendaDetailAction
    class UpdateTitle(val newTitle: String) : AgendaDetailAction
    class UpdateDescription(val newDescription: String) : AgendaDetailAction
    class UpdateDate(val newDate: LocalDate) : AgendaDetailAction
    class UpdateTime(val newTime: LocalTime) : AgendaDetailAction
    class UpdateEventEndDate(val newDate: LocalDate) : AgendaDetailAction
    class UpdateEventEndTime(val newTime: LocalTime) : AgendaDetailAction
    class UpdateReminder(val newReminder: ReminderType) : AgendaDetailAction
    data object SaveEvent : AgendaDetailAction
    data object SaveTask : AgendaDetailAction
    data object SaveReminder : AgendaDetailAction

    class UpdateAttendeeSelection(val selection: AttendeeSelection) : AgendaDetailAction
    class RemoveAttendee(val userId: String) : AgendaDetailAction
}