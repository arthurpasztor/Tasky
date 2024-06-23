package com.example.tasky.agenda.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tasky.R
import com.example.tasky.agenda.domain.AgendaItemType
import com.example.tasky.agenda.domain.DetailItemType
import com.example.tasky.agenda.domain.ReminderType
import com.example.tasky.agenda.domain.model.Attendee
import com.example.tasky.agenda.presentation.composables.detail.AddPhotoEmptySpaceSection
import com.example.tasky.agenda.presentation.composables.detail.HeaderSection
import com.example.tasky.agenda.presentation.composables.detail.AttendeeSection
import com.example.tasky.agenda.presentation.composables.detail.DateTimeSection
import com.example.tasky.agenda.presentation.composables.detail.DeleteSection
import com.example.tasky.agenda.presentation.composables.detail.DescriptionSection
import com.example.tasky.agenda.presentation.composables.utils.HorizontalDividerGray1dp
import com.example.tasky.agenda.presentation.composables.detail.LabelSection
import com.example.tasky.agenda.presentation.composables.detail.ReminderSelectorSection
import com.example.tasky.agenda.presentation.composables.detail.TitleSection
import com.example.tasky.auth.presentation.showToast
import com.example.tasky.core.presentation.ObserveAsEvents
import com.example.tasky.destinations.TextEditorRootDestination
import com.example.tasky.ui.theme.BackgroundBlack
import com.example.tasky.ui.theme.BackgroundWhite
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
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

            is AgendaDetailVMAction.RemoveAgendaItemSuccess -> {
                context.showToast(
                    when (destination.itemType) {
                        AgendaItemType.EVENT -> TODO()
                        AgendaItemType.TASK -> R.string.success_task_removed
                        AgendaItemType.REMINDER -> R.string.success_reminder_removed
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

@Preview()
@Composable
private fun AgendaDetailScreenPreview() {
    AgendaDetailScreen(
        state = AgendaDetailsState(
            itemId = "123",
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundBlack)
    ) {
        HeaderSection(
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
                .verticalScroll(rememberScrollState())
                .background(BackgroundWhite)
        ) {
            LabelSection(state)

            TitleSection(state, onAction)

            HorizontalDividerGray1dp()

            DescriptionSection(state, onAction)

            HorizontalDividerGray1dp()

            if (state.isEvent()) {
                if (state.isUserEventCreator && (state.isCreateMode() || state.isEditMode())) {
                    AddPhotoEmptySpaceSection()
                }
            }

            DateTimeSection(state = state, onAction = onAction)

            HorizontalDividerGray1dp()

            if (state.isEvent()) {
                DateTimeSection(state = state, isEndDate = true, onAction = onAction)

                HorizontalDividerGray1dp()
            }

            ReminderSelectorSection(
                modifier = Modifier.padding(top = 20.dp, bottom = 20.dp, start = 16.dp, end = 34.dp),
                state = state,
                onAction = onAction
            )

            HorizontalDividerGray1dp()

            if (state.isEvent()) {
                AttendeeSection(state, onAction)
            }

            if (state.isViewMode() || state.isEditMode()) {
                Spacer(Modifier.weight(1f))

                DeleteSection(state, onAction)
            }
        }
    }
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
    data object RemoveAgendaItem : AgendaDetailAction
    data object SaveEvent : AgendaDetailAction
    data object SaveTask : AgendaDetailAction
    data object SaveReminder : AgendaDetailAction

    class UpdateAttendeeSelection(val selection: AttendeeSelection) : AgendaDetailAction
    class UpdateNewAttendeeEmail(val email: String) : AgendaDetailAction
    data object ClearNewAttendeeEmail : AgendaDetailAction
    data object AddAttendee : AgendaDetailAction
    class RemoveAttendee(val userId: String) : AgendaDetailAction
}