package com.example.tasky.agenda.presentation.composables.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tasky.agenda.presentation.AgendaDetailAction
import com.example.tasky.agenda.presentation.AgendaDetailsState
import com.example.tasky.agenda.presentation.composables.utils.ArrowEditButton
import com.example.tasky.ui.theme.detailDescriptionStyle

@Preview
@Composable
private fun DescriptionSectionPreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        val state = AgendaDetailsState(description = "Description preview")

        DescriptionSection(state) {}
    }
}

@Composable
fun DescriptionSection(
    state: AgendaDetailsState,
    onAction: (AgendaDetailAction) -> Unit
) {
    Row(modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)) {
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
            onAction.invoke(AgendaDetailAction.OpenDescriptionEditor)
        }
    }
}
