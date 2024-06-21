package com.example.tasky.agenda.presentation.cutils

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tasky.R
import com.example.tasky.agenda.domain.AgendaItemType
import com.example.tasky.agenda.domain.formatHeaderDate
import com.example.tasky.agenda.presentation.AgendaDetailAction
import com.example.tasky.agenda.presentation.AgendaDetailsState
import com.example.tasky.ui.theme.VeryLightGray
import com.example.tasky.ui.theme.headerStyle
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.time.LocalDate

@Preview
@Composable
fun AgendaItemDetailHeaderPreview() {
    Box(modifier = Modifier.background(Color.Black)) {
        AgendaItemDetailHeader(
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
fun AgendaItemDetailHeader(
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
