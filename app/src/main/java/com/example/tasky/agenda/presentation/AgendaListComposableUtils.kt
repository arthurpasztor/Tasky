package com.example.tasky.agenda.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tasky.R
import com.example.tasky.ui.theme.ReminderGray
import com.example.tasky.ui.theme.TaskyGreen
import com.example.tasky.ui.theme.agendaListContentStyle
import com.example.tasky.ui.theme.agendaListTitleStyle
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.rememberMaterialDialogState

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
        is AgendaItemUi.TaskUi -> TaskyGreen
        else -> ReminderGray
    }

    val headerColor = when (item) {
        is AgendaItemUi.TaskUi -> Color.White
        else -> Color.Black
    }

    val contentColor = when (item) {
        is AgendaItemUi.TaskUi -> Color.White
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

@Preview
@Composable
fun PullToRefreshLazyColumnPreview() {
    PullToRefreshLazyColumn(
        modifier = Modifier.fillMaxSize(),
        items = getAgendaSample(),
        content = {
            AgendaItem(
                item = it,
                onDoneRadioButtonClicked = {},
                onOpen = { _, _ -> },
                onEdit = { _, _ -> },
                onDelete = { _, _ -> }
            )
        },
        needleContent = {
            Needle()
        },
        isSelectedDateToday = true,
        isRefreshing = false,
        onRefresh = {}
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun <T : AgendaItemUi> PullToRefreshLazyColumn(
    modifier: Modifier = Modifier,
    items: List<T>,
    content: @Composable (T) -> Unit,
    needleContent: @Composable () -> Unit,
    isSelectedDateToday: Boolean,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    lazyListState: LazyListState = rememberLazyListState()
) {
    val pullToRefreshState = rememberPullToRefreshState()
    Box(modifier = modifier.nestedScroll(pullToRefreshState.nestedScrollConnection)) {
        LazyColumn(
            state = lazyListState,
            contentPadding = PaddingValues(8.dp),
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (isSelectedDateToday) {
                stickyHeader {
                    Today()
                }
            }
            items(items) {
                when (it) {
                    is AgendaItemUi.NeedleUi -> needleContent()
                    else -> content(it)
                }
            }
        }

        if (pullToRefreshState.isRefreshing) {
            LaunchedEffect(true) {
                onRefresh()
            }
        }

        LaunchedEffect(isRefreshing) {
            if (isRefreshing) {
                pullToRefreshState.startRefresh()
            } else {
                pullToRefreshState.endRefresh()
            }
        }

        PullToRefreshContainer(
            modifier = Modifier.align(Alignment.TopCenter),
            state = pullToRefreshState,
            containerColor = Color.White
        )
    }
}

@Preview
@Composable
private fun Today() {
    Text(
        text = stringResource(id = R.string.today),
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(8.dp),
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Black
    )
}

@Preview
@Composable
fun Needle() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(14.dp)
            .background(Color.White)
    ) {
        Canvas(modifier = Modifier
            .size(14.dp)
            .align(Alignment.CenterVertically),
            onDraw = {
                drawCircle(color = Color.Black)
            })
        HorizontalDivider(
            modifier = Modifier
                .align(Alignment.CenterVertically),
            color = Color.Black,
            thickness = 4.dp
        )
    }
}
