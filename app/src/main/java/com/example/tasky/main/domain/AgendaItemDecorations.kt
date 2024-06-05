package com.example.tasky.main.domain

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import com.example.tasky.main.data.dto.AgendaListItem
import com.example.tasky.main.data.dto.TaskDTO
import com.example.tasky.ui.theme.ReminderGray
import com.example.tasky.ui.theme.TaskyGreen

fun <T : AgendaListItem> T.getAgendaItemBackgroundColor() = when (this) {
    is TaskDTO -> TaskyGreen
    else -> ReminderGray
}

fun <T : AgendaListItem> T.getAgendaItemHeaderColor() = when (this) {
    is TaskDTO -> Color.White
    else -> Color.Black
}

fun <T : AgendaListItem> T.getAgendaItemContentColor() = when (this) {
    is TaskDTO -> Color.White
    else -> Color.Gray
}

fun <T : AgendaListItem> T.getAgendaItemTitle() = when {
    this.isItemDone() -> {
        buildAnnotatedString {
            withStyle(
                style = SpanStyle(
                    textDecoration = TextDecoration.LineThrough
                )
            ) {
                append(this@getAgendaItemTitle.getItemTitle())
            }
        }
    }

    else -> AnnotatedString(this.getItemTitle())
}