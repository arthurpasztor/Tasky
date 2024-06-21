package com.example.tasky.agenda.presentation.cutils

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.example.tasky.R
import com.example.tasky.agenda.domain.getInitials
import com.example.tasky.agenda.presentation.AgendaAction
import com.example.tasky.agenda.presentation.AgendaState
import com.example.tasky.ui.theme.BackgroundBlack
import com.example.tasky.ui.theme.Purple40
import com.example.tasky.ui.theme.PurpleGrey80
import com.example.tasky.ui.theme.VeryLightGray

@Preview
@Composable
fun ProfileIcon(
    modifier: Modifier = Modifier,
    state: AgendaState = AgendaState(userName = "Arthur Pasztor"),
    onAction: (AgendaAction) -> Unit = {}
) {
    var isContextMenuVisible by rememberSaveable {
        mutableStateOf(false)
    }
    var pressOffset by remember {
        mutableStateOf(DpOffset.Zero)
    }
    var itemHeight by remember {
        mutableStateOf(0.dp)
    }
    val density = LocalDensity.current

    Box(
        modifier = modifier
            .onSizeChanged {
                itemHeight = with(density) { it.height.toDp() }
            }
    ) {
        Box(
            modifier = modifier
                .size(dimensionResource(R.dimen.profile_icon_size))
                .clip(CircleShape)
                .background(PurpleGrey80)
                .pointerInput(true) {
                    detectTapGestures(
                        onPress = {
                            isContextMenuVisible = true
                            pressOffset = DpOffset(it.x.toDp(), it.y.toDp())
                        },
                    )
                }
        ) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = state.userName.getInitials(),
                color = Purple40
            )
        }
        DropdownMenu(
            expanded = isContextMenuVisible,
            onDismissRequest = {
                isContextMenuVisible = false
            },
            offset = pressOffset.copy(
                y = pressOffset.y - itemHeight
            )
        ) {
            DropdownMenuItem(
                text = { Text(text = stringResource(id = R.string.log_out)) },
                onClick = {
                    onAction.invoke(AgendaAction.LogOut)
                    isContextMenuVisible = false
                })
        }
    }
}

@Preview
@Composable
fun AddButton(
    modifier: Modifier = Modifier,
    onAction: (AgendaAction) -> Unit = {}
) {
    var isContextMenuVisible by rememberSaveable {
        mutableStateOf(false)
    }
    var pressOffset by remember {
        mutableStateOf(DpOffset.Zero)
    }
    var itemHeight by remember {
        mutableStateOf(0.dp)
    }
    val density = LocalDensity.current

    Box(
        modifier = modifier
            .onSizeChanged {
                itemHeight = with(density) { it.height.toDp() }
            }
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(25))
                .size(60.dp)
                .background(BackgroundBlack)
                .pointerInput(true) {
                    detectTapGestures(
                        onPress = {
                            isContextMenuVisible = true
                            pressOffset = DpOffset(it.x.toDp(), it.y.toDp())
                        }
                    )
                }
        ) {
            Icon(
                modifier = Modifier.align(Alignment.Center),
                imageVector = Icons.Default.Add,
                contentDescription = "plus",
                tint = Color.White
            )
        }

        DropdownMenu(
            expanded = isContextMenuVisible,
            onDismissRequest = {
                isContextMenuVisible = false
            },
            offset = pressOffset.copy(
                y = pressOffset.y - itemHeight
            )
        ) {
            DropdownMenuItem(
                text = { Text(text = stringResource(id = R.string.event)) },
                onClick = {
                    onAction.invoke(AgendaAction.CreateNewEvent)
                    isContextMenuVisible = false
                })
            DropdownMenuItem(
                text = { Text(text = stringResource(id = R.string.task)) },
                onClick = {
                    onAction.invoke(AgendaAction.CreateNewTask)
                    isContextMenuVisible = false
                })
            DropdownMenuItem(
                text = { Text(text = stringResource(id = R.string.reminder)) },
                onClick = {
                    onAction.invoke(AgendaAction.CreateNewReminder)
                    isContextMenuVisible = false
                })
        }
    }
}

@Preview
@Composable
fun AgendaItemMoreButton(
    modifier: Modifier = Modifier,
    tint: Color = Color.White,
    onOpen: () -> Unit = {},
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    var isContextMenuVisible by rememberSaveable {
        mutableStateOf(false)
    }
    var pressOffset by remember {
        mutableStateOf(DpOffset.Zero)
    }
    var itemHeight by remember {
        mutableStateOf(0.dp)
    }
    val density = LocalDensity.current

    Box(
        modifier = modifier
            .onSizeChanged {
                itemHeight = with(density) { it.height.toDp() }
            }
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .pointerInput(true) {
                    detectTapGestures(
                        onPress = {
                            isContextMenuVisible = true
                            pressOffset = DpOffset(it.x.toDp(), it.y.toDp())
                        }
                    )
                },
        ) {
            Icon(
                modifier = Modifier.align(Alignment.Center),
                imageVector = Icons.Filled.MoreHoriz,
                contentDescription = "menu",
                tint = tint,
            )
        }

        DropdownMenu(
            expanded = isContextMenuVisible,
            onDismissRequest = {
                isContextMenuVisible = false
            },
            offset = pressOffset.copy(
                y = pressOffset.y - itemHeight
            )
        ) {
            DropdownMenuItem(
                text = { Text(text = stringResource(id = R.string.open)) },
                onClick = {
                    onOpen.invoke()
                    isContextMenuVisible = false
                })
            DropdownMenuItem(
                text = { Text(text = stringResource(id = R.string.edit)) },
                onClick = {
                    onEdit.invoke()
                    isContextMenuVisible = false
                })
            DropdownMenuItem(
                text = { Text(text = stringResource(id = R.string.delete)) },
                onClick = {
                    onDelete.invoke()
                    isContextMenuVisible = false
                })
        }
    }
}

@Preview
@Composable
fun CloseButton(onAction: () -> Unit = {}) {
    IconButton(
        modifier = Modifier
            .size(60.dp)
            .padding(8.dp),
        onClick = {
            onAction.invoke()
        }) {
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "cancel",
            tint = Color.White,
        )
    }
}

@Preview
@Composable
fun ArrowEditButton(
    onAction: () -> Unit = {}
) {
    IconButton(
        modifier = Modifier.size(60.dp),
        onClick = {
            onAction.invoke()
        }) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "edit",
            tint = Color.Black,
        )
    }
}

@Preview
@Composable
fun ArrowBackButton(
    onAction: () -> Unit = {}
) {
    IconButton(
        modifier = Modifier.size(60.dp),
        onClick = {
            onAction.invoke()
        }) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
            contentDescription = "back",
            tint = Color.Black,
        )
    }
}

@Preview
@Composable
fun HorizontalDividerGray1dp() {
    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = VeryLightGray, thickness = 1.dp)
}

