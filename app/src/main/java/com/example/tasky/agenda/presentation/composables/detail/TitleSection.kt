package com.example.tasky.agenda.presentation.composables.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.RadioButton
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
import com.example.tasky.ui.theme.detailTitleStyle

@Preview
@Composable
private fun TitleSectionPreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        val state = AgendaDetailsState(title = "Title preview",)

        TitleSection(state) {}
    }
}

@Composable
fun TitleSection(
    state: AgendaDetailsState,
    onAction: (AgendaDetailAction) -> Unit
) {
    Row(modifier = Modifier.padding(16.dp)) {
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
            onAction.invoke(AgendaDetailAction.OpenTitleEditor)
        }
    }
}