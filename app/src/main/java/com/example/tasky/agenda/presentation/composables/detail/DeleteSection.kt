package com.example.tasky.agenda.presentation.composables.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tasky.R
import com.example.tasky.agenda.domain.AgendaItemType
import com.example.tasky.agenda.presentation.AgendaDetailAction
import com.example.tasky.agenda.presentation.AgendaDetailsState
import com.example.tasky.ui.theme.VeryLightGray
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.rememberMaterialDialogState

@Preview
@Composable
private fun DeleteSectionPreview() {
    Box(modifier = Modifier.background(Color.White)) {
        DeleteSection(AgendaDetailsState()) {}
    }
}

@Composable
fun DeleteSection(
    state: AgendaDetailsState,
    onAction: (AgendaDetailAction) -> Unit
) {
    val deleteAlertDialogState = rememberMaterialDialogState()

    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 16.dp),
        color = VeryLightGray,
        thickness = 1.dp
    )

    val bottomActionButtonText = stringResource(
        id = when (state.agendaItemType) {
            AgendaItemType.EVENT -> R.string.delete_event
            AgendaItemType.TASK -> R.string.delete_task
            AgendaItemType.REMINDER -> R.string.delete_reminder
        }
    ).uppercase()
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { deleteAlertDialogState.show() }
            .padding(vertical = 22.dp),
        text = bottomActionButtonText,
        textAlign = TextAlign.Center,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = Color.LightGray
    )

    // region Delete Alert Dialog
    MaterialDialog(
        dialogState = deleteAlertDialogState,
        buttons = {
            positiveButton(text = stringResource(id = R.string.confirm)) {
                deleteAlertDialogState.hide()
                onAction.invoke(AgendaDetailAction.RemoveAgendaItem)
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
    // endregion
}
