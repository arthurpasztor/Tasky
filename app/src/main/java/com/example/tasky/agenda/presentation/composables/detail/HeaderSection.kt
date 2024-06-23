package com.example.tasky.agenda.presentation.composables.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tasky.R
import com.example.tasky.agenda.domain.AgendaItemType
import com.example.tasky.agenda.domain.formatHeaderDate
import com.example.tasky.agenda.presentation.AgendaDetailsState
import com.example.tasky.agenda.presentation.composables.utils.CloseButton
import com.example.tasky.ui.theme.headerStyle
import java.time.LocalDate

@Preview
@Composable
private fun HeaderSectionPreview() {
    Box(modifier = Modifier.background(Color.Black)) {
        HeaderSection(
            state = AgendaDetailsState(
                agendaItemType = AgendaItemType.REMINDER,
                itemId = null,
                date = LocalDate.now()
            ),
            onNavigateBack = {},
            onSwitchToEditMode = {},
            onSave = {}
        )
    }
}

@Composable
fun HeaderSection(
    state: AgendaDetailsState,
    onNavigateBack: () -> Unit,
    onSwitchToEditMode: () -> Unit,
    onSave: () -> Unit
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