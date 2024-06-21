package com.example.tasky.agenda.presentation.cutils

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.example.tasky.R
import com.example.tasky.agenda.domain.ReminderType
import com.example.tasky.agenda.domain.formatDetailDate
import com.example.tasky.agenda.domain.formatDetailTime
import com.example.tasky.agenda.presentation.AgendaDetailAction
import com.example.tasky.agenda.presentation.AgendaDetailsState
import com.example.tasky.ui.theme.VeryLightGray
import com.example.tasky.ui.theme.detailDescriptionStyle
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.time.LocalDate

@Preview
@Composable
private fun DateTimeSectionPreview() {
    Box(modifier = Modifier.background(Color.White)) {
        DateTimeSection(state = AgendaDetailsState()) {}
    }
}

@Composable
fun DateTimeSection(
    state: AgendaDetailsState,
    isEndDate: Boolean = false,
    onAction: (AgendaDetailAction) -> Unit
) {
    val dateDialogState = rememberMaterialDialogState()
    val timeDialogState = rememberMaterialDialogState()

    val initialDate = if (state.isEvent() && isEndDate) {
        state.eventDate
    } else {
        state.date
    }
    val initialTime = if (state.isEvent() && isEndDate) {
        state.eventTime
    } else {
        state.time
    }

    Row(modifier = Modifier.padding(vertical = 20.dp, horizontal = 16.dp)) {
        val label = stringResource(
            when {
                state.isEvent() -> {
                    if (isEndDate) R.string.to else R.string.from
                }

                else -> R.string.at
            }
        )

        Text(
            modifier = Modifier.align(Alignment.CenterVertically),
            text = label,
            style = detailDescriptionStyle,
            maxLines = 1
        )
        Spacer(Modifier.weight(1f))
        ClickableText(
            modifier = Modifier.align(Alignment.CenterVertically),
            text = AnnotatedString(initialTime.formatDetailTime()),
            style = detailDescriptionStyle,
            maxLines = 1,
            onClick = {
                timeDialogState.show()
            }
        )
        Spacer(Modifier.weight(1f))
        ClickableText(
            modifier = Modifier.align(Alignment.CenterVertically),
            text = AnnotatedString(initialDate.formatDetailDate()),
            style = detailDescriptionStyle,
            maxLines = 1,
            onClick = {
                dateDialogState.show()
            }
        )
        Spacer(Modifier.weight(1f))
    }

    //region DateTime Picker Dialogs
    val now = LocalDate.now()

    val earliestAvailableDate = if (state.isEvent() && isEndDate) {
        state.date
    } else {
        now
    }

    MaterialDialog(
        dialogState = dateDialogState,
        buttons = {
            positiveButton(text = stringResource(id = R.string.ok))
            negativeButton(text = stringResource(id = R.string.cancel))
        }
    ) {
        datepicker(
            initialDate = initialDate,
            title = stringResource(id = R.string.pick_a_date),
            allowedDateValidator = {
                it.isAfter(earliestAvailableDate) || it.isEqual(earliestAvailableDate)
            }
        ) {
            if (state.isEvent() && isEndDate) {
                onAction(AgendaDetailAction.UpdateEventEndDate(it))
            } else {
                onAction(AgendaDetailAction.UpdateDate(it))
            }
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
            initialTime = initialTime,
            title = stringResource(id = R.string.pick_a_time)
        ) {
            if (state.isEvent() && isEndDate) {
                onAction(AgendaDetailAction.UpdateEventEndTime(it))
            } else {
                onAction(AgendaDetailAction.UpdateTime(it))
            }
        }
    }
    //endregion
}

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

