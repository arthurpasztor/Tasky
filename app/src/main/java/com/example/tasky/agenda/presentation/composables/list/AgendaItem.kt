package com.example.tasky.agenda.presentation.composables.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tasky.R
import com.example.tasky.agenda.presentation.AgendaItemUi
import com.example.tasky.agenda.presentation.AgendaItemUiType
import com.example.tasky.agenda.presentation.composables.utils.AgendaItemMoreButton
import com.example.tasky.agenda.presentation.getEventSample
import com.example.tasky.agenda.presentation.getReminderSample
import com.example.tasky.agenda.presentation.getTaskSample
import com.example.tasky.ui.theme.EventGreen
import com.example.tasky.ui.theme.ReminderGray
import com.example.tasky.ui.theme.TaskyGreen
import com.example.tasky.ui.theme.agendaListContentStyle
import com.example.tasky.ui.theme.agendaListTitleStyle
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.rememberMaterialDialogState

@Preview
@Composable
private fun EventItemPreview() {
    AgendaItem(
        item = getEventSample(),
        onDoneRadioButtonClicked = {},
        onOpen = { _, _ -> },
        onEdit = { _, _ -> },
        onDelete = { _, _ -> }
    )
}

@Preview
@Composable
private fun TaskItemPreview() {
    AgendaItem(
        item = getTaskSample(),
        onDoneRadioButtonClicked = {},
        onOpen = { _, _ -> },
        onEdit = { _, _ -> },
        onDelete = { _, _ -> }
    )
}

@Preview
@Composable
private fun ReminderItemPreview() {
    AgendaItem(
        item = getReminderSample(),
        onDoneRadioButtonClicked = {},
        onOpen = { _, _ -> },
        onEdit = { _, _ -> },
        onDelete = { _, _ -> }
    )
}

@Composable
fun <T : AgendaItemUi> AgendaItem(
    item: T,
    onDoneRadioButtonClicked: (AgendaItemUi.TaskUi) -> Unit,
    onOpen: (itemId: String, itemType: AgendaItemUiType) -> Unit,
    onEdit: (itemId: String, itemType: AgendaItemUiType) -> Unit,
    onDelete: (itemId: String, itemType: AgendaItemUiType) -> Unit
) {
    val deleteAlertDialogState = rememberMaterialDialogState()

    val backgroundColor = when (item) {
        is AgendaItemUi.EventUi -> EventGreen
        is AgendaItemUi.TaskUi -> TaskyGreen
        is AgendaItemUi.ReminderUi -> ReminderGray
        else -> ReminderGray
    }

    val headerColor = when (item) {
        is AgendaItemUi.EventUi -> Color.Black
        is AgendaItemUi.TaskUi -> Color.White
        is AgendaItemUi.ReminderUi -> Color.Black
        else -> Color.Black
    }

    val contentColor = when (item) {
        is AgendaItemUi.EventUi -> Color.Gray
        is AgendaItemUi.TaskUi -> Color.White
        is AgendaItemUi.ReminderUi -> Color.Gray
        else -> Color.Gray
    }

    val title = when {
        item.isDone -> {
            buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        textDecoration = TextDecoration.LineThrough
                    )
                ) {
                    append(item.title)
                }
            }
        }

        else -> AnnotatedString(item.title)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(15))
            .background(backgroundColor)
    ) {
        Column {

            Row(modifier = Modifier) {
                RadioButton(
                    modifier = Modifier
                        .size(18.dp)
                        .padding(start = 20.dp, top = 25.dp)
                        .align(Alignment.Top),
                    selected = item.isDone,
                    colors = RadioButtonDefaults.colors(
                        selectedColor = headerColor,
                        unselectedColor = headerColor
                    ),
                    onClick = {
                        if (item is AgendaItemUi.TaskUi) {
                            onDoneRadioButtonClicked.invoke(item)
                        }
                    }
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.Top)
                        .weight(1f)
                        .padding(start = 25.dp)
                ) {
                    Text(
                        modifier = Modifier.padding(top = 15.dp),
                        text = title,
                        style = agendaListTitleStyle,
                        color = headerColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(top = 15.dp),
                        text = item.description,
                        style = agendaListContentStyle,
                        color = contentColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                AgendaItemMoreButton(
                    modifier = Modifier.align(Alignment.Top),
                    tint = headerColor,
                    onOpen = { onOpen.invoke(item.id, item.getAgendaItemType()) },
                    onEdit = { onEdit.invoke(item.id, item.getAgendaItemType()) },
                    onDelete = { deleteAlertDialogState.show() }
                )
            }

            Text(
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(top = 25.dp, bottom = 15.dp, end = 20.dp),
                text = item.getFormattedTime(),
                style = agendaListContentStyle,
                color = contentColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }

    MaterialDialog(
        dialogState = deleteAlertDialogState,
        buttons = {
            positiveButton(text = stringResource(id = R.string.confirm)) {
                deleteAlertDialogState.hide()
                onDelete.invoke(item.id, item.getAgendaItemType())
            }
            negativeButton(text = stringResource(id = R.string.cancel)) {
                deleteAlertDialogState.hide()
            }
        }
    ) {
        Text(
            modifier = Modifier.padding(16.dp),
            text = stringResource(id = R.string.delete_item_confirmation),
        )
    }
}