package com.example.tasky.agenda.presentation

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.example.tasky.R
import com.example.tasky.agenda.domain.AgendaItemType
import com.example.tasky.agenda.domain.DetailInteractionMode
import com.example.tasky.agenda.domain.ReminderType
import com.example.tasky.agenda.domain.formatHeaderDate
import com.example.tasky.agenda.domain.getInitials
import com.example.tasky.ui.theme.BackgroundBlack
import com.example.tasky.ui.theme.Purple40
import com.example.tasky.ui.theme.PurpleGrey80
import com.example.tasky.ui.theme.VeryLightGray
import com.example.tasky.ui.theme.detailDescriptionStyle
import com.example.tasky.ui.theme.headerStyle
import java.time.LocalDate

@Preview
@Composable
fun ReminderSelector(
    modifier: Modifier = Modifier,
    state: TaskReminderState = TaskReminderState(),
    onAction: (TaskReminderAction) -> Unit = {}
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
                        onAction.invoke(TaskReminderAction.UpdateReminder(it))
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
fun CloseButton(
    modifier: Modifier = Modifier,
    onAction: () -> Unit = {}
) {
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
    agendaItemType: AgendaItemType = AgendaItemType.REMINDER,
    interactionMode: DetailInteractionMode = DetailInteractionMode.CREATE,
    headerDate: LocalDate = LocalDate.now(),
    onNavigateBack: () -> Unit = {},
    onSwitchToEditMode: () -> Unit = {},
    onSave: () -> Unit = {}
) {
    val headerPadding = dimensionResource(R.dimen.padding_20)

    val editHeader =
        stringResource(id = if (agendaItemType == AgendaItemType.TASK) R.string.edit_task else R.string.edit_reminder)
    val headerText = when (interactionMode) {
        DetailInteractionMode.CREATE -> LocalDate.now().formatHeaderDate()
        DetailInteractionMode.EDIT -> editHeader.uppercase()
        DetailInteractionMode.VIEW -> headerDate.formatHeaderDate()
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
        when (interactionMode) {
            DetailInteractionMode.CREATE, DetailInteractionMode.EDIT -> {
                ClickableText(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(end = headerPadding),
                    text = AnnotatedString(stringResource(id = R.string.save)),
                    style = headerStyle,
                    onClick = { onSave.invoke() }
                )
            }

            DetailInteractionMode.VIEW -> {
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
        }
    }
}