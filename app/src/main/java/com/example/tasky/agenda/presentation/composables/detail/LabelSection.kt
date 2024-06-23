package com.example.tasky.agenda.presentation.composables.detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tasky.R
import com.example.tasky.agenda.domain.AgendaItemType
import com.example.tasky.agenda.presentation.AgendaDetailsState
import com.example.tasky.ui.theme.EventGreen
import com.example.tasky.ui.theme.ReminderBorderGray
import com.example.tasky.ui.theme.ReminderGray
import com.example.tasky.ui.theme.TaskyGreen
import com.example.tasky.ui.theme.detailTypeStyle

@Preview
@Composable
private fun LabelSectionPreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        val state = AgendaDetailsState()

        LabelSection(state)
    }
}

@Composable
fun LabelSection(state: AgendaDetailsState) {
    Row(modifier = Modifier.padding(top = 20.dp, start = 16.dp, end = 16.dp)) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(10))
                .size(18.dp)
                .background(
                    color = when (state.agendaItemType) {
                        AgendaItemType.EVENT -> EventGreen
                        AgendaItemType.TASK -> TaskyGreen
                        AgendaItemType.REMINDER -> ReminderGray
                    }
                )
                .border(
                    BorderStroke(
                        width = 1.dp,
                        color = when (state.agendaItemType) {
                            AgendaItemType.EVENT -> EventGreen
                            AgendaItemType.TASK -> TaskyGreen
                            AgendaItemType.REMINDER -> ReminderBorderGray
                        }
                    )
                )
                .align(Alignment.CenterVertically),
        )
        Text(
            modifier = Modifier
                .padding(start = 10.dp)
                .align(Alignment.CenterVertically),
            text = stringResource(
                id = when (state.agendaItemType) {
                    AgendaItemType.EVENT -> R.string.event
                    AgendaItemType.TASK -> R.string.task
                    AgendaItemType.REMINDER -> R.string.reminder
                }
            ),
            style = detailTypeStyle,
        )
    }
}