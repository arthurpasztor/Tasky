package com.example.tasky.main.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tasky.main.domain.AgendaDM
import com.example.tasky.main.domain.AgendaListItem
import com.example.tasky.main.domain.AgendaListItem.Needle
import com.example.tasky.main.domain.AgendaListItem.ReminderDM
import com.example.tasky.main.domain.AgendaListItem.TaskDM
import com.example.tasky.ui.theme.ReminderGray
import com.example.tasky.ui.theme.TaskyGreen
import com.example.tasky.ui.theme.agendaListContentStyle
import com.example.tasky.ui.theme.agendaListTitleStyle

@Preview
@Composable
private fun TaskItemPreview() {
    AgendaItem(item = TaskDM.getSampleTask())
}

@Preview
@Composable
private fun ReminderItemPreview() {
    AgendaItem(item = ReminderDM.getSampleReminder())
}

@Composable
fun <T : AgendaListItem> AgendaItem(item: T) {
    val backgroundColor = when (item) {
        is TaskDM -> TaskyGreen
        else -> ReminderGray
    }

    val headerColor = when (item) {
        is TaskDM -> Color.White
        else -> Color.Black
    }

    val contentColor = when (item) {
        is TaskDM -> Color.White
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
                    onClick = { }
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
                IconButton(
                    modifier = Modifier
                        .align(Alignment.Top)
                        .size(60.dp),
                    onClick = {
                    }) {
                    Icon(
                        imageVector = Icons.Filled.MoreHoriz,
                        contentDescription = "menu",
                        tint = headerColor,
                    )
                }
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
}

@Preview
@Composable
fun PullToRefreshLazyColumnPreview() {
    PullToRefreshLazyColumn(
        modifier = Modifier.fillMaxSize(),
        items = AgendaDM.getSample().items,
        content = {
            AgendaItem(it)
        },
        needleContent = {
            Text(
                text = "Needle",
                modifier = Modifier.background(Color.Red)
            )
        },
        isRefreshing = false,
        onRefresh = {}
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T : AgendaListItem> PullToRefreshLazyColumn(
    modifier: Modifier = Modifier,
    items: List<T>,
    content: @Composable (T) -> Unit,
    needleContent: @Composable () -> Unit,
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
            items(items) {
                when (it) {
                    is Needle -> needleContent()
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
