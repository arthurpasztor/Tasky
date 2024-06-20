package com.example.tasky.agenda.presentation

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.example.tasky.R
import com.example.tasky.agenda.domain.AgendaItemType
import com.example.tasky.agenda.domain.ReminderType
import com.example.tasky.agenda.domain.formatHeaderDate
import com.example.tasky.agenda.domain.getInitials
import com.example.tasky.agenda.domain.model.Attendee
import com.example.tasky.ui.theme.BackgroundBlack
import com.example.tasky.ui.theme.BackgroundGray
import com.example.tasky.ui.theme.Purple40
import com.example.tasky.ui.theme.PurpleGrey80
import com.example.tasky.ui.theme.VeryLightGray
import com.example.tasky.ui.theme.attendeeLabelStyle
import com.example.tasky.ui.theme.detailDescriptionStyle
import com.example.tasky.ui.theme.headerStyle
import com.example.tasky.ui.theme.toggleSelectedStyle
import com.example.tasky.ui.theme.toggleUnselectedStyle
import java.time.LocalDate

@Preview
@Composable
fun ReminderSelector(
    modifier: Modifier = Modifier,
    state: AgendaDetailsState = AgendaDetailsState(),
    onAction: (AgendaDetailAction) -> Unit = {}
) {
    val context = LocalContext.current

    var isContextMenuVisible by rememberSaveable {
        mutableStateOf(false)
    }
    var pressOffset by remember {
        mutableStateOf(DpOffset.Zero)
    }
    var itemHeight by remember {
        mutableStateOf(0.dp)
    }
    val density = LocalDensity.current

    Box(
        modifier = modifier
            .onSizeChanged {
                itemHeight = with(density) { it.height.toDp() }
            }
    ) {
        Row(modifier = Modifier
            .pointerInput(true) {
                detectTapGestures(
                    onPress = {
                        isContextMenuVisible = true
                        pressOffset = DpOffset(it.x.toDp(), it.y.toDp())
                    },
                )
            }) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10))
                    .size(22.dp)
                    .background(VeryLightGray)
                    .align(Alignment.CenterVertically),
            ) {
                Icon(
                    imageVector = Icons.Default.NotificationsNone,
                    contentDescription = "checkIcon",
                    tint = Color.LightGray
                )
            }
            Text(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(start = 10.dp),
                text = state.reminderType.getReminderString(context),
                style = detailDescriptionStyle,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.weight(1f))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "edit",
                tint = Color.Black,
            )
        }
        DropdownMenu(
            expanded = isContextMenuVisible,
            onDismissRequest = {
                isContextMenuVisible = false
            },
            offset = pressOffset.copy(
                y = pressOffset.y - itemHeight
            )
        ) {
            ReminderType.entries.forEach {
                DropdownMenuItem(
                    text = { Text(text = it.getReminderString(context)) },
                    onClick = {
                        onAction.invoke(AgendaDetailAction.UpdateReminder(it))
                        isContextMenuVisible = false
                    })
            }
        }
    }
}

private fun ReminderType.getReminderString(context: Context): String {
    return context.getString(
        when (this) {
            ReminderType.MINUTES_10 -> R.string.reminder_10_minutes
            ReminderType.MINUTES_30 -> R.string.reminder_30_minutes
            ReminderType.HOUR_1 -> R.string.reminder_1_hour
            ReminderType.HOUR_6 -> R.string.reminder_6_hours
            ReminderType.DAY_1 -> R.string.reminder_1_day
        }
    )
}

@Preview
@Composable
fun ProfileIcon(
    modifier: Modifier = Modifier,
    state: AgendaState = AgendaState(userName = "Arthur Pasztor"),
    onAction: (AgendaAction) -> Unit = {}
) {
    var isContextMenuVisible by rememberSaveable {
        mutableStateOf(false)
    }
    var pressOffset by remember {
        mutableStateOf(DpOffset.Zero)
    }
    var itemHeight by remember {
        mutableStateOf(0.dp)
    }
    val density = LocalDensity.current

    Box(
        modifier = modifier
            .onSizeChanged {
                itemHeight = with(density) { it.height.toDp() }
            }
    ) {
        Box(
            modifier = modifier
                .size(dimensionResource(R.dimen.profile_icon_size))
                .clip(CircleShape)
                .background(PurpleGrey80)
                .pointerInput(true) {
                    detectTapGestures(
                        onPress = {
                            isContextMenuVisible = true
                            pressOffset = DpOffset(it.x.toDp(), it.y.toDp())
                        },
                    )
                }
        ) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = state.userName.getInitials(),
                color = Purple40
            )
        }
        DropdownMenu(
            expanded = isContextMenuVisible,
            onDismissRequest = {
                isContextMenuVisible = false
            },
            offset = pressOffset.copy(
                y = pressOffset.y - itemHeight
            )
        ) {
            DropdownMenuItem(
                text = { Text(text = stringResource(id = R.string.log_out)) },
                onClick = {
                    onAction.invoke(AgendaAction.LogOut)
                    isContextMenuVisible = false
                })
        }
    }
}

@Preview
@Composable
fun AddButton(
    modifier: Modifier = Modifier,
    onAction: (AgendaAction) -> Unit = {}
) {
    var isContextMenuVisible by rememberSaveable {
        mutableStateOf(false)
    }
    var pressOffset by remember {
        mutableStateOf(DpOffset.Zero)
    }
    var itemHeight by remember {
        mutableStateOf(0.dp)
    }
    val density = LocalDensity.current

    Box(
        modifier = modifier
            .onSizeChanged {
                itemHeight = with(density) { it.height.toDp() }
            }
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(25))
                .size(60.dp)
                .background(BackgroundBlack)
                .pointerInput(true) {
                    detectTapGestures(
                        onPress = {
                            isContextMenuVisible = true
                            pressOffset = DpOffset(it.x.toDp(), it.y.toDp())
                        }
                    )
                }
        ) {
            Icon(
                modifier = Modifier.align(Alignment.Center),
                imageVector = Icons.Default.Add,
                contentDescription = "plus",
                tint = Color.White
            )
        }

        DropdownMenu(
            expanded = isContextMenuVisible,
            onDismissRequest = {
                isContextMenuVisible = false
            },
            offset = pressOffset.copy(
                y = pressOffset.y - itemHeight
            )
        ) {
            DropdownMenuItem(
                text = { Text(text = stringResource(id = R.string.event)) },
                onClick = {
                    onAction.invoke(AgendaAction.CreateNewEvent)
                    isContextMenuVisible = false
                })
            DropdownMenuItem(
                text = { Text(text = stringResource(id = R.string.task)) },
                onClick = {
                    onAction.invoke(AgendaAction.CreateNewTask)
                    isContextMenuVisible = false
                })
            DropdownMenuItem(
                text = { Text(text = stringResource(id = R.string.reminder)) },
                onClick = {
                    onAction.invoke(AgendaAction.CreateNewReminder)
                    isContextMenuVisible = false
                })
        }
    }
}

@Preview
@Composable
fun AgendaItemMoreButton(
    modifier: Modifier = Modifier,
    tint: Color = Color.White,
    onOpen: () -> Unit = {},
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    var isContextMenuVisible by rememberSaveable {
        mutableStateOf(false)
    }
    var pressOffset by remember {
        mutableStateOf(DpOffset.Zero)
    }
    var itemHeight by remember {
        mutableStateOf(0.dp)
    }
    val density = LocalDensity.current

    Box(
        modifier = modifier
            .onSizeChanged {
                itemHeight = with(density) { it.height.toDp() }
            }
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .pointerInput(true) {
                    detectTapGestures(
                        onPress = {
                            isContextMenuVisible = true
                            pressOffset = DpOffset(it.x.toDp(), it.y.toDp())
                        }
                    )
                },
        ) {
            Icon(
                modifier = Modifier.align(Alignment.Center),
                imageVector = Icons.Filled.MoreHoriz,
                contentDescription = "menu",
                tint = tint,
            )
        }

        DropdownMenu(
            expanded = isContextMenuVisible,
            onDismissRequest = {
                isContextMenuVisible = false
            },
            offset = pressOffset.copy(
                y = pressOffset.y - itemHeight
            )
        ) {
            DropdownMenuItem(
                text = { Text(text = stringResource(id = R.string.open)) },
                onClick = {
                    onOpen.invoke()
                    isContextMenuVisible = false
                })
            DropdownMenuItem(
                text = { Text(text = stringResource(id = R.string.edit)) },
                onClick = {
                    onEdit.invoke()
                    isContextMenuVisible = false
                })
            DropdownMenuItem(
                text = { Text(text = stringResource(id = R.string.delete)) },
                onClick = {
                    onDelete.invoke()
                    isContextMenuVisible = false
                })
        }
    }
}

@Preview
@Composable
fun CloseButton(onAction: () -> Unit = {}) {
    IconButton(
        modifier = Modifier
            .size(60.dp)
            .padding(8.dp),
        onClick = {
            onAction.invoke()
        }) {
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "cancel",
            tint = Color.White,
        )
    }
}

@Preview
@Composable
fun ArrowEditButton(
    onAction: () -> Unit = {}
) {
    IconButton(
        modifier = Modifier.size(60.dp),
        onClick = {
            onAction.invoke()
        }) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "edit",
            tint = Color.Black,
        )
    }
}

@Preview
@Composable
fun ArrowBackButton(
    onAction: () -> Unit = {}
) {
    IconButton(
        modifier = Modifier.size(60.dp),
        onClick = {
            onAction.invoke()
        }) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
            contentDescription = "back",
            tint = Color.Black,
        )
    }
}

@Preview
@Composable
fun AgendaItemDetailHeader(
    state: AgendaDetailsState = AgendaDetailsState(
        agendaItemType = AgendaItemType.REMINDER,
        itemId = null,
        date = LocalDate.now()
    ),
    onNavigateBack: () -> Unit = {},
    onSwitchToEditMode: () -> Unit = {},
    onSave: () -> Unit = {}
) {
    val headerPadding = dimensionResource(R.dimen.padding_20)

    val editHeader = stringResource(
        id = when (state.agendaItemType) {
            AgendaItemType.EVENT -> R.string.edit_event
            AgendaItemType.TASK -> R.string.edit_task
            AgendaItemType.REMINDER -> R.string.edit_reminder
        }
    )

    val headerText = when {
        state.isCreateMode() -> LocalDate.now().formatHeaderDate()
        state.isEditMode() -> editHeader.uppercase()
        state.isViewMode() -> state.date.formatHeaderDate()
        else -> ""
    }

    Row {
        CloseButton { onNavigateBack.invoke() }
        Spacer(Modifier.weight(1f))
        Text(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(end = headerPadding),
            text = headerText,
            style = headerStyle
        )
        Spacer(Modifier.weight(1f))
        when {
            state.isViewMode() -> {
                IconButton(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .size(60.dp)
                        .padding(8.dp),
                    onClick = { onSwitchToEditMode.invoke() }
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "create, edit",
                        tint = Color.White,
                    )
                }
            }

            else -> {
                ClickableText(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(end = headerPadding),
                    text = AnnotatedString(stringResource(id = R.string.save)),
                    style = headerStyle,
                    onClick = { onSave.invoke() }
                )
            }
        }
    }
}

@Preview
@Composable
fun AddAttendeeButton(modifier: Modifier = Modifier, onAction: () -> Unit = {}) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(25))
            .background(BackgroundGray)
            .clickable { onAction.invoke() },
    ) {
        Icon(
            modifier = Modifier.align(Alignment.Center),
            imageVector = Icons.Default.Add,
            contentDescription = "add attendee",
            tint = Color.Gray,
        )
    }
}

@Preview
@Composable
fun AttendeeToggleToolbar(
    state: AgendaDetailsState = AgendaDetailsState(
        extras = AgendaItemDetails.EventItemDetail(attendeeSelection = AttendeeSelection.ALL)
    ),
    onAction: (AgendaDetailAction) -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .clip(RoundedCornerShape(50))
                .background(if (state.isAllAttendeesSelected()) Color.Black else BackgroundGray)
                .padding(vertical = 8.dp)
                .weight(1f)
                .clickable {
                    onAction(AgendaDetailAction.UpdateAttendeeSelection(AttendeeSelection.ALL))
                },
            text = stringResource(id = R.string.all),
            style = if (state.isAllAttendeesSelected()) toggleSelectedStyle else toggleUnselectedStyle,
        )
        Spacer(modifier = Modifier.size(16.dp))
        Text(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .clip(RoundedCornerShape(50))
                .background(if (state.isGoingAttendeesSelected()) Color.Black else BackgroundGray)
                .padding(vertical = 8.dp)
                .weight(1f)
                .clickable {
                    onAction(AgendaDetailAction.UpdateAttendeeSelection(AttendeeSelection.GOING))
                },
            text = stringResource(id = R.string.going),
            style = if (state.isGoingAttendeesSelected()) toggleSelectedStyle else toggleUnselectedStyle,
        )
        Spacer(modifier = Modifier.size(16.dp))
        Text(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .clip(RoundedCornerShape(50))
                .background(if (state.isNotGoingAttendeesSelected()) Color.Black else BackgroundGray)
                .padding(vertical = 8.dp)
                .weight(1f)
                .clickable {
                    onAction.invoke(AgendaDetailAction.UpdateAttendeeSelection(AttendeeSelection.NOT_GOING))
                },
            text = stringResource(id = R.string.not_going),
            style = if (state.isNotGoingAttendeesSelected()) toggleSelectedStyle else toggleUnselectedStyle,
        )
    }
}

@Preview
@Composable
private fun AttendeeListPreview() {
    AttendeeList(
        modifier = Modifier,
        labelRes = R.string.going,
        list = listOf(Attendee.getSampleAttendeeGoing(), Attendee.getSampleAttendeeNotGoing()),
        creatorFullName = "Michael Scott",
        onRemoveAttendee = {})
}

@Composable
fun AttendeeList(
    modifier: Modifier = Modifier,
    labelRes: Int,
    list: List<Attendee>,
    creatorFullName: String? = null,
    onRemoveAttendee: (userId: String) -> Unit,
) {
    if (list.isNotEmpty() || creatorFullName != null) {
        Text(
            modifier = modifier.padding(top = 16.dp, bottom = 8.dp),
            text = stringResource(id = labelRes),
            style = attendeeLabelStyle,
        )
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            creatorFullName?.let {
                AttendeeItem(
                    modifier = Modifier,
                    fullName = it,
                    isUserEventCreator = true,
                    onRemoveAttendee = {}
                )
            }
            list.forEach {
                AttendeeItem(
                    modifier = Modifier,
                    fullName = it.fullName,
                    isUserEventCreator = false,
                    onRemoveAttendee = {
                        onRemoveAttendee.invoke(it.userId)
                    }
                )
            }
        }
    }
}

@Preview
@Composable
private fun AttendeeItemCreatorPreview() {
    AttendeeItem(
        modifier = Modifier,
        fullName = "Dwight Schrute",
        isUserEventCreator = true,
        onRemoveAttendee = { }
    )
}

@Preview
@Composable
private fun AttendeeItemParticipantPreview() {
    AttendeeItem(
        modifier = Modifier,
        fullName = "Jim Halpert",
        isUserEventCreator = false,
        onRemoveAttendee = { }
    )
}

@Composable
fun AttendeeItem(
    modifier: Modifier,
    fullName: String,
    isUserEventCreator: Boolean,
    onRemoveAttendee: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(25))
            .background(BackgroundGray)
    ) {
        AttendeeInitials(Modifier.padding(4.dp), fullName)
        Text(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(horizontal = 8.dp)
                .weight(1f),
            text = fullName,
            color = Color.DarkGray
        )
        if (isUserEventCreator) {
            Text(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(end = 16.dp),
                text = stringResource(id = R.string.creator),
                color = Color.LightGray
            )
        } else {
            Icon(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(end = 16.dp)
                    .clickable {
                        onRemoveAttendee.invoke()
                    },
                imageVector = Icons.Outlined.Delete,
                contentDescription = "remove attendee",
                tint = Color.Gray,
            )
        }
    }
}

@Preview
@Composable
private fun AttendeeInitials(modifier: Modifier = Modifier, fullName: String = "Dwight Shrute") {
    Box(
        modifier = modifier
            .size(dimensionResource(R.dimen.profile_icon_size))
            .clip(CircleShape)
            .background(Color.LightGray)
    ) {
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = fullName.getInitials(),
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}