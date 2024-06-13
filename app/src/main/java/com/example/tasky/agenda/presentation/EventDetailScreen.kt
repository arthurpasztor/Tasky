package com.example.tasky.agenda.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tasky.R
import com.example.tasky.agenda.domain.AgendaItemType
import com.example.tasky.agenda.domain.DetailInteractionMode
import com.example.tasky.agenda.domain.DetailItemType
import com.example.tasky.destinations.TextEditorRootDestination
import com.example.tasky.ui.theme.BackgroundBlack
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
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf

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
                .padding(horizontal = 16.dp)
        ) {
            Row(modifier = Modifier.padding(top = 20.dp)) {
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
                    onAction.invoke(EventAction.OpenTitleEditor)
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
                    onAction.invoke(EventAction.OpenDescriptionEditor)
                }
            }

            HorizontalDivider(color = VeryLightGray, thickness = 1.dp)
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
}