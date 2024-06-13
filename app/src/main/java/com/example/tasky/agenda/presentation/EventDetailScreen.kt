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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tasky.R
import com.example.tasky.agenda.domain.AgendaItemType
import com.example.tasky.agenda.domain.DetailInteractionMode
import com.example.tasky.agenda.domain.DetailItemType
import com.example.tasky.agenda.domain.ReminderType
import com.example.tasky.agenda.domain.formatDetailDate
import com.example.tasky.agenda.domain.formatDetailTime
import com.example.tasky.destinations.TextEditorRootDestination
import com.example.tasky.ui.theme.BackgroundBlack
import com.example.tasky.ui.theme.BackgroundGray
import com.example.tasky.ui.theme.BackgroundWhite
import com.example.tasky.ui.theme.EventGreen
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
fun EventDetailRoot(
    navigator: DestinationsNavigator,
    resultRecipient: ResultRecipient<TextEditorRootDestination, TextEditorResponse>,
    mode: DetailInteractionMode,
    itemId: String? = null
) {

    val TAG = "EventDetailScreen"

    val context = LocalContext.current
    val viewModel: AgendaDetailsViewModel =
        getViewModel(parameters = { parametersOf(AgendaItemType.EVENT, mode, itemId) })
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(true) {
        viewModel.eventNavChannel.collect { destination ->
            when (destination) {
                EventVMAction.OpenTitleEditor -> {
                    navigator.navigate(
                        TextEditorRootDestination(
                            text = state.title,
                            type = DetailItemType.TITLE,
                        )
                    )
                }

                EventVMAction.OpenDescriptionEditor -> {
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
                DetailItemType.TITLE -> viewModel.onAction(EventAction.UpdateTitle(result.value.newText))
                DetailItemType.DESCRIPTION -> viewModel.onAction(EventAction.UpdateDescription(result.value.newText))
            }
        }
    }

    EventDetailScreen(
        state = state,
        onAction = viewModel::onAction,
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
private fun EventDetailScreen(
    state: AgendaItemState = AgendaItemState(),
    onAction: (EventAction) -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    val cornerRadius = dimensionResource(R.dimen.radius_30)

    val dateFromDialogState = rememberMaterialDialogState()
    val timeFromDialogState = rememberMaterialDialogState()
    val dateToDialogState = rememberMaterialDialogState()
    val timeToDialogState = rememberMaterialDialogState()

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
            onSwitchToEditMode = { onAction(EventAction.SwitchToEditMode) },
            onSave = { onAction(EventAction.SaveEvent) }
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(cornerRadius, cornerRadius, 0.dp, 0.dp))
                .background(BackgroundWhite)
        ) {
            Row(modifier = Modifier.padding(top = 20.dp, start = 16.dp, end = 16.dp)) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10))
                        .size(18.dp)
                        .background(EventGreen)
                        .align(Alignment.CenterVertically),
                )
                Text(
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .align(Alignment.CenterVertically),
                    text = stringResource(R.string.event),
                    style = detailTypeStyle,
                )
            }

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
                    onAction.invoke(EventAction.OpenTitleEditor)
                }
            }

            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = VeryLightGray, thickness = 1.dp)

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
                    onAction.invoke(EventAction.OpenDescriptionEditor)
                }
            }

            if (state.isUserEventCreator
                && (state.interactionMode == DetailInteractionMode.CREATE
                        || state.interactionMode == DetailInteractionMode.EDIT)
            ) {
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
            } else {
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = VeryLightGray,
                    thickness = 1.dp
                )
            }

            Row(modifier = Modifier.padding(vertical = 20.dp, horizontal = 16.dp)) {
                Text(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    text = stringResource(id = R.string.from),
                    style = detailDescriptionStyle,
                    maxLines = 1
                )
                Spacer(Modifier.weight(1f))
                ClickableText(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    text = AnnotatedString(state.fromTime.formatDetailTime()),
                    style = detailDescriptionStyle,
                    maxLines = 1,
                    onClick = {
                        timeFromDialogState.show()
                    }
                )
                Spacer(Modifier.weight(1f))
                ClickableText(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    text = AnnotatedString(state.fromDate.formatDetailDate()),
                    style = detailDescriptionStyle,
                    maxLines = 1,
                    onClick = {
                        dateFromDialogState.show()
                    }
                )
                Spacer(Modifier.weight(1f))
            }

            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = VeryLightGray, thickness = 1.dp)

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
                    text = AnnotatedString(state.toTime.formatDetailTime()),
                    style = detailDescriptionStyle,
                    maxLines = 1,
                    onClick = {
                        timeToDialogState.show()
                    }
                )
                Spacer(Modifier.weight(1f))
                ClickableText(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    text = AnnotatedString(state.toDate.formatDetailDate()),
                    style = detailDescriptionStyle,
                    maxLines = 1,
                    onClick = {
                        dateToDialogState.show()
                    }
                )
                Spacer(Modifier.weight(1f))
            }

            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp),color = VeryLightGray, thickness = 1.dp)

            ReminderSelector(
                modifier = Modifier.padding(vertical = 20.dp, horizontal = 16.dp),
                state = state
            ) {
                onAction.invoke(EventAction.UpdateReminder(it))
            }

            HorizontalDivider(color = VeryLightGray, thickness = 1.dp)
        }
    }

    val now = LocalDate.now()

    MaterialDialog(
        dialogState = dateFromDialogState,
        buttons = {
            positiveButton(text = stringResource(id = R.string.ok))
            negativeButton(text = stringResource(id = R.string.cancel))
        }
    ) {
        datepicker(
            initialDate = state.fromDate,
            title = stringResource(id = R.string.pick_a_date),
            allowedDateValidator = {
                it.isAfter(now) || it.isEqual(now)
            }
        ) {
            onAction(EventAction.UpdateFromDate(it))
        }
    }

    MaterialDialog(
        dialogState = timeFromDialogState,
        buttons = {
            positiveButton(text = stringResource(id = R.string.ok))
            negativeButton(text = stringResource(id = R.string.cancel))
        }
    ) {
        timepicker(
            initialTime = state.fromTime,
            title = stringResource(id = R.string.pick_a_time)
        ) {
            onAction(EventAction.UpdateFromTime(it))
        }
    }

    MaterialDialog(
        dialogState = dateToDialogState,
        buttons = {
            positiveButton(text = stringResource(id = R.string.ok))
            negativeButton(text = stringResource(id = R.string.cancel))
        }
    ) {
        datepicker(
            initialDate = state.toDate,
            title = stringResource(id = R.string.pick_a_date),
            allowedDateValidator = {
                it.isAfter(state.fromDate) || it.isEqual(state.fromDate)
            }
        ) {
            onAction(EventAction.UpdateToDate(it))
        }
    }

    MaterialDialog(
        dialogState = timeToDialogState,
        buttons = {
            positiveButton(text = stringResource(id = R.string.ok))
            negativeButton(text = stringResource(id = R.string.cancel))
        }
    ) {
        timepicker(
            initialTime = state.toTime,
            title = stringResource(id = R.string.pick_a_time)
        ) {
            onAction(EventAction.UpdateToTime(it))
        }
    }
}

sealed class EventAction {
    data object OpenTitleEditor : EventAction()
    data object OpenDescriptionEditor : EventAction()
    data object SwitchToEditMode : EventAction()
    class UpdateTitle(val newTitle: String) : EventAction()
    class UpdateDescription(val newDescription: String) : EventAction()
    data object SaveEvent : EventAction()
    class UpdateFromDate(val newDate: LocalDate) : EventAction()
    class UpdateFromTime(val newTime: LocalTime) : EventAction()
    class UpdateToDate(val newDate: LocalDate) : EventAction()
    class UpdateToTime(val newTime: LocalTime) : EventAction()
    class UpdateReminder(val newReminder: ReminderType) : EventAction()
}