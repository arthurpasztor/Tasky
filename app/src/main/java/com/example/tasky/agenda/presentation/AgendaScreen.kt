package com.example.tasky.agenda.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tasky.R
import com.example.tasky.agenda.domain.AgendaItemType
import com.example.tasky.agenda.domain.isToday
import com.example.tasky.agenda.presentation.composables.list.AgendaItem
import com.example.tasky.agenda.presentation.composables.list.Needle
import com.example.tasky.agenda.presentation.composables.list.PullToRefreshLazyColumn
import com.example.tasky.agenda.presentation.composables.utils.AddButton
import com.example.tasky.agenda.presentation.composables.utils.ProfileIcon
import com.example.tasky.auth.presentation.showToast
import com.example.tasky.core.presentation.ObserveAsEvents
import com.example.tasky.ui.theme.BackgroundBlack
import com.example.tasky.ui.theme.BackgroundWhite
import com.example.tasky.ui.theme.SelectedDateYellow
import com.example.tasky.ui.theme.UnselectedDateTransparent
import com.example.tasky.ui.theme.headerStyle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.AgendaDetailRootDestination
import com.ramcosta.composedestinations.generated.destinations.AgendaRootDestination
import com.ramcosta.composedestinations.generated.destinations.LoginRootDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDate
import java.util.Locale

@Destination<RootGraph>
@Composable
fun AgendaRoot(navigator: DestinationsNavigator) {

    val TAG = "AgendaScreen"

    val context = LocalContext.current
    val viewModel: AgendaViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.navChannel) { destination ->
        when (destination) {
            AgendaResponseAction.HandleLogoutResponseSuccess -> {
                viewModel.onAction(AgendaAction.ClearUserData)
                navigator.navigate(LoginRootDestination) {
                    popUpTo(AgendaRootDestination) {
                        inclusive = true
                    }
                }
            }

            is AgendaResponseAction.AgendaError -> context.showToast(destination.error, TAG)

            is AgendaResponseAction.CreateNewAgendaItem -> {
                navigator.navigate(
                    AgendaDetailRootDestination(type = destination.itemType)
                ) {
                    popUpTo(LoginRootDestination) {
                        inclusive = true
                    }
                }
            }

            is AgendaResponseAction.OpenAgendaItemDetail -> {
                navigator.navigate(
                    AgendaDetailRootDestination(
                        type = destination.itemType,
                        itemId = destination.itemId,
                        editable = destination.isEditable
                    )
                )
            }
        }
    }

    AgendaScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Preview
@Composable
private fun AgendaScreen(
    state: AgendaState = AgendaState(userName = "Arthur"),
    onAction: (AgendaAction) -> Unit = {}
) {
    val cornerRadius = dimensionResource(R.dimen.radius_30)
    val monthPadding = dimensionResource(R.dimen.padding_20)

    val dateDialogState = rememberMaterialDialogState()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundBlack)
        ) {
            Row {
                ClickableText(
                    modifier = Modifier.padding(start = monthPadding, top = monthPadding, bottom = monthPadding),
                    text = AnnotatedString(state.selectedDate.month.name.uppercase(Locale.getDefault())),
                    style = headerStyle,
                    onClick = {
                        dateDialogState.show()
                    }
                )
                Icon(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "arrowDropDownIcon",
                    tint = Color.White
                )
                Spacer(Modifier.weight(1f))
                ProfileIcon(
                    modifier = Modifier
                        .padding(end = dimensionResource(R.dimen.padding_8))
                        .align(Alignment.CenterVertically),
                    state = state,
                    onAction = onAction
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(cornerRadius, cornerRadius, 0.dp, 0.dp))
                    .background(BackgroundWhite)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 100.dp)
                ) {
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

                    PullToRefreshLazyColumn(
                        items = state.dailyAgenda.toAgendaItemUiList(),
                        content = { item ->
                            AgendaItem(
                                item = item,
                                onDoneRadioButtonClicked = { taskUi ->
                                    onAction.invoke(AgendaAction.SetTaskDone(taskUi))
                                },
                                onOpen = { id, type ->
                                    type.toAgendaItemType()?.let { onAction.invoke(AgendaAction.Open(id, it)) }
                                },
                                onEdit = { id, type ->
                                    type.toAgendaItemType()?.let { onAction.invoke(AgendaAction.Edit(id, it)) }
                                },
                                onDelete = { id, type ->
                                    type.toAgendaItemType()?.let { onAction.invoke(AgendaAction.Delete(id, it)) }
                                }
                            )
                        },
                        needleContent = {
                            Needle()
                        },
                        isRefreshing = state.isRefreshing,
                        onRefresh = {
                            onAction.invoke(AgendaAction.PullToRefresh)
                        },
                        isSelectedDateToday = state.selectedDate.isToday()
                    )
                }

                WeekHeader(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = dimensionResource(id = R.dimen.padding_20)),
                    state = state,
                    onAction = onAction
                )
            }
        }

        MaterialDialog(
            dialogState = dateDialogState,
            buttons = {
                positiveButton(text = stringResource(id = R.string.ok))
                negativeButton(text = stringResource(id = R.string.cancel))
            }
        ) {
            datepicker(
                initialDate = state.selectedDate,
                title = stringResource(id = R.string.pick_a_date)
            ) {
                onAction(AgendaAction.UpdateSelectedDate(it, true))
            }
        }

        AddButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(
                    bottom = dimensionResource(id = R.dimen.padding_40),
                    end = dimensionResource(id = R.dimen.padding_20)
                ),
            onAction = onAction
        )
    }
}

sealed interface AgendaAction {
    data object LogOut : AgendaAction
    data object ClearUserData : AgendaAction
    class UpdateSelectedDate(val newSelection: LocalDate, val forceSelectedDateToFirstPosition: Boolean) :
        AgendaAction

    data object PullToRefresh : AgendaAction

    data object CreateNewEvent : AgendaAction
    data object CreateNewTask : AgendaAction
    data object CreateNewReminder : AgendaAction

    class SetTaskDone(val task: AgendaItemUi.TaskUi) : AgendaAction
    class Open(val itemId: String, val itemType: AgendaItemType) : AgendaAction
    class Edit(val itemId: String, val itemType: AgendaItemType) : AgendaAction
    class Delete(val itemId: String, val itemType: AgendaItemType) : AgendaAction
}

@Preview
@Composable
fun WeekHeader(
    modifier: Modifier = Modifier,
    state: AgendaState = AgendaState(userName = "Arthur"),
    onAction: (AgendaAction) -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = dimensionResource(id = R.dimen.padding_8))
    ) {
        val firstDay = state.firstDateOfHeader

        repeat(6) { index ->
            val nextDay = firstDay.plusDays(index.toLong())
            DayBubble(
                modifier = Modifier.weight(1f),
                day = nextDay,
                isSelected = nextDay == state.selectedDate,
            ) {
                onAction(AgendaAction.UpdateSelectedDate(it, false))
            }
        }
    }
}

@Preview
@Composable
fun DayBubble(
    modifier: Modifier = Modifier,
    day: LocalDate = LocalDate.now(),
    isSelected: Boolean = true,
    onDaySelected: (selectedDay: LocalDate) -> Unit = {}
) {
    Column(
        modifier = modifier
            .width(dimensionResource(R.dimen.day_bubble_width))
            .height(dimensionResource(R.dimen.day_bubble_height))
            .padding(horizontal = dimensionResource(id = R.dimen.padding_8))
            .clip(CircleShape)
            .background(if (isSelected) SelectedDateYellow else UnselectedDateTransparent)
            .clickable {
                if (!isSelected) onDaySelected(day)
            },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = day.dayOfWeek.name.first().toString(),
            color = Color.Gray
        )
        Text(
            text = day.dayOfMonth.toString(),
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            fontSize = dimensionResource(R.dimen.font_size_16).value.sp
        )
    }
}
