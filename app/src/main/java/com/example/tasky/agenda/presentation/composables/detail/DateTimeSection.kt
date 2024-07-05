package com.example.tasky.agenda.presentation.composables.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tasky.R
import com.example.tasky.agenda.domain.formatDetailDate
import com.example.tasky.agenda.domain.formatDetailTime
import com.example.tasky.agenda.presentation.AgendaDetailAction
import com.example.tasky.agenda.presentation.AgendaDetailsState
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

    val initialDate = if (isEndDate) {
        state.eventEndDate
    } else {
        state.date
    }
    val initialTime = if (isEndDate) {
        state.eventEndTime
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
        Text(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .clickable {
                    timeDialogState.show()
                },
            text = AnnotatedString(initialTime.formatDetailTime()),
            style = detailDescriptionStyle,
            maxLines = 1
        )
        Spacer(Modifier.weight(1f))
        Text(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .clickable {
                    dateDialogState.show()
                },
            text = AnnotatedString(initialDate.formatDetailDate()),
            style = detailDescriptionStyle,
            maxLines = 1
        )
        Spacer(Modifier.weight(1f))
    }

    //region DateTime Picker Dialogs
    val now = LocalDate.now()

    val earliestAvailableDate = if (isEndDate) {
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
            if (isEndDate) {
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
            if (isEndDate) {
                onAction(AgendaDetailAction.UpdateEventEndTime(it))
            } else {
                onAction(AgendaDetailAction.UpdateTime(it))
            }
        }
    }
    //endregion
}
