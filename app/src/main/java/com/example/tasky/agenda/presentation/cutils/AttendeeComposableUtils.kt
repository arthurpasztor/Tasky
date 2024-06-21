package com.example.tasky.agenda.presentation.cutils

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tasky.R
import com.example.tasky.agenda.domain.getInitials
import com.example.tasky.agenda.domain.model.Attendee
import com.example.tasky.agenda.presentation.AgendaDetailAction
import com.example.tasky.agenda.presentation.AgendaDetailsState
import com.example.tasky.agenda.presentation.AgendaItemDetails
import com.example.tasky.agenda.presentation.AttendeeSelection
import com.example.tasky.ui.theme.BackgroundGray
import com.example.tasky.ui.theme.attendeeLabelStyle
import com.example.tasky.ui.theme.detailTitleStyle
import com.example.tasky.ui.theme.toggleSelectedStyle
import com.example.tasky.ui.theme.toggleUnselectedStyle

@Composable
fun AttendeeSection(state: AgendaDetailsState, onAction: (AgendaDetailAction) -> Unit) {
    AttendeeHeader(state, onAction)
    AttendeeToggleToolbar(state, onAction)
    AttendeeFullList(state, onAction)
}

@Preview
@Composable
private fun AttendeeHeaderPreview() {
    AttendeeHeader(state = AgendaDetailsState(itemId = null), onAction = {})
}

@Composable
fun AttendeeHeader(state: AgendaDetailsState, onAction: (AgendaDetailAction) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 26.dp, bottom = 26.dp)
    ) {
        Text(
            modifier = Modifier.align(Alignment.CenterVertically),
            text = stringResource(id = R.string.visitors),
            style = detailTitleStyle,
            fontSize = 22.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.size(16.dp))
        if (state.isCreateMode() || state.isEditMode()) {
            AddAttendeeButton(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .size(30.dp)
            ) {
                // TODO add visitor
            }
        }
    }
}

@Preview
@Composable
fun AddAttendeeButton(modifier: Modifier = Modifier, onAction: () -> Unit = {}) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(25))
            .background(BackgroundGray)
            .clickable { onAction.invoke() },
    ) {
        Icon(
            modifier = Modifier.align(Alignment.Center),
            imageVector = Icons.Default.Add,
            contentDescription = "add attendee",
            tint = Color.Gray,
        )
    }
}

@Preview
@Composable
private fun AttendeeToggleToolbarPreview() {
    AttendeeToggleToolbar(
        state = AgendaDetailsState(
            extras = AgendaItemDetails.EventItemDetail(attendeeSelection = AttendeeSelection.ALL)
        ),
        onAction = {}
    )
}

@Composable
fun AttendeeToggleToolbar(state: AgendaDetailsState, onAction: (AgendaDetailAction) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .clip(RoundedCornerShape(50))
                .background(if (state.isAllAttendeesSelected) Color.Black else BackgroundGray)
                .padding(vertical = 8.dp)
                .weight(1f)
                .clickable {
                    onAction(AgendaDetailAction.UpdateAttendeeSelection(AttendeeSelection.ALL))
                },
            text = stringResource(id = R.string.all),
            style = if (state.isAllAttendeesSelected) toggleSelectedStyle else toggleUnselectedStyle,
        )
        Spacer(modifier = Modifier.size(16.dp))
        Text(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .clip(RoundedCornerShape(50))
                .background(if (state.isGoingAttendeesSelected) Color.Black else BackgroundGray)
                .padding(vertical = 8.dp)
                .weight(1f)
                .clickable {
                    onAction(AgendaDetailAction.UpdateAttendeeSelection(AttendeeSelection.GOING))
                },
            text = stringResource(id = R.string.going),
            style = if (state.isGoingAttendeesSelected) toggleSelectedStyle else toggleUnselectedStyle,
        )
        Spacer(modifier = Modifier.size(16.dp))
        Text(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .clip(RoundedCornerShape(50))
                .background(if (state.isNotGoingAttendeesSelected) Color.Black else BackgroundGray)
                .padding(vertical = 8.dp)
                .weight(1f)
                .clickable {
                    onAction.invoke(AgendaDetailAction.UpdateAttendeeSelection(AttendeeSelection.NOT_GOING))
                },
            text = stringResource(id = R.string.not_going),
            style = if (state.isNotGoingAttendeesSelected) toggleSelectedStyle else toggleUnselectedStyle,
        )
    }
}

@Preview
@Composable
private fun AttendeeFullListPreview() {
    AttendeeFullList(
        state = AgendaDetailsState(
            extras = AgendaItemDetails.EventItemDetail(attendeeSelection = AttendeeSelection.ALL)
        ),
        onAction = {}
    )
}

@Composable
fun AttendeeFullList(state: AgendaDetailsState, onAction: (AgendaDetailAction) -> Unit) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        if (state.isAllAttendeesSelected || state.isGoingAttendeesSelected) {
            AttendeeList(
                modifier = Modifier,
                labelRes = R.string.going,
                list = state.attendees,
                creatorFullName = state.currentUserFullNameIfEventCreator,
                onRemoveAttendee = {
                    onAction(AgendaDetailAction.RemoveAttendee(it))
                }
            )
        }
        if (state.isAllAttendeesSelected || state.isNotGoingAttendeesSelected) {
            AttendeeList(
                modifier = Modifier.align(Alignment.Start),
                labelRes = R.string.not_going,
                list = state.nonAttendees,
                onRemoveAttendee = {
                    onAction(AgendaDetailAction.RemoveAttendee(it))
                }
            )
        }
    }
}

@Preview
@Composable
private fun AttendeeListPreview() {
    AttendeeList(
        modifier = Modifier,
        labelRes = R.string.going,
        list = listOf(Attendee.getSampleAttendeeGoing(), Attendee.getSampleAttendeeNotGoing()),
        creatorFullName = "Michael Scott",
        onRemoveAttendee = {})
}

@Composable
fun AttendeeList(
    modifier: Modifier = Modifier,
    labelRes: Int,
    list: List<Attendee>,
    creatorFullName: String? = null,
    onRemoveAttendee: (userId: String) -> Unit,
) {
    if (list.isNotEmpty() || creatorFullName != null) {
        Text(
            modifier = modifier.padding(top = 16.dp, bottom = 8.dp),
            text = stringResource(id = labelRes),
            style = attendeeLabelStyle,
        )
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            creatorFullName?.let {
                AttendeeItem(
                    modifier = Modifier,
                    fullName = it,
                    isUserEventCreator = true,
                    onRemoveAttendee = {}
                )
            }
            list.forEach {
                AttendeeItem(
                    modifier = Modifier,
                    fullName = it.fullName,
                    isUserEventCreator = false,
                    onRemoveAttendee = {
                        onRemoveAttendee.invoke(it.userId)
                    }
                )
            }
        }
    }
}

@Preview
@Composable
private fun AttendeeItemCreatorPreview() {
    AttendeeItem(
        modifier = Modifier,
        fullName = "Dwight Schrute",
        isUserEventCreator = true,
        onRemoveAttendee = { }
    )
}

@Preview
@Composable
private fun AttendeeItemParticipantPreview() {
    AttendeeItem(
        modifier = Modifier,
        fullName = "Jim Halpert",
        isUserEventCreator = false,
        onRemoveAttendee = { }
    )
}

@Composable
fun AttendeeItem(
    modifier: Modifier,
    fullName: String,
    isUserEventCreator: Boolean,
    onRemoveAttendee: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(25))
            .background(BackgroundGray)
    ) {
        AttendeeInitials(Modifier.padding(4.dp), fullName)
        Text(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(horizontal = 8.dp)
                .weight(1f),
            text = fullName,
            color = Color.DarkGray
        )
        if (isUserEventCreator) {
            Text(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(end = 16.dp),
                text = stringResource(id = R.string.creator),
                color = Color.LightGray
            )
        } else {
            Icon(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(end = 16.dp)
                    .clickable {
                        onRemoveAttendee.invoke()
                    },
                imageVector = Icons.Outlined.Delete,
                contentDescription = "remove attendee",
                tint = Color.Gray,
            )
        }
    }
}

@Preview
@Composable
private fun AttendeeInitials(modifier: Modifier = Modifier, fullName: String = "Dwight Shrute") {
    Box(
        modifier = modifier
            .size(dimensionResource(R.dimen.profile_icon_size))
            .clip(CircleShape)
            .background(Color.LightGray)
    ) {
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = fullName.getInitials(),
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}