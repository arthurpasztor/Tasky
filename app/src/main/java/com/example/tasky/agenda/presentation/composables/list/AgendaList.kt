package com.example.tasky.agenda.presentation.composables.list

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tasky.R
import com.example.tasky.agenda.presentation.AgendaItemUi
import com.example.tasky.agenda.presentation.getAgendaSample

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
            items(items = items, key = { it.id }) {
                when (it) {
                    is AgendaItemUi.NeedleUi ->
                        Card(modifier = Modifier.animateItem()) {
                            needleContent()
                        }

                    else ->
                        Card(
                            modifier = Modifier
                                .animateItem()
                                .clip(RoundedCornerShape(15))
                        ) {
                            content(it)
                        }
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
