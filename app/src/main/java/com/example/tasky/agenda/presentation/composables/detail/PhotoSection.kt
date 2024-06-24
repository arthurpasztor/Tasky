package com.example.tasky.agenda.presentation.composables.detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.tasky.R
import com.example.tasky.agenda.domain.model.Photo
import com.example.tasky.agenda.presentation.AgendaDetailsState
import com.example.tasky.agenda.presentation.AgendaItemDetails
import com.example.tasky.ui.theme.BackgroundGray

private const val MAX_NUMBER_OF_PHOTOS = 10

@Preview
@Composable
private fun PhotoSectionPreview() {
    PhotoSection(
        state = AgendaDetailsState(
            extras = AgendaItemDetails.EventItemDetail(
                photos = listOf(Photo("key1", "uri1"), Photo("key2", "uri2"))
            )
        ),
        onOpenGallery = {},
        onOpenFullScreenImage = {}
    )
}

@Composable
fun PhotoSection(
    state: AgendaDetailsState,
    onOpenGallery: () -> Unit,
    onOpenFullScreenImage: (photo: Photo) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .background(BackgroundGray)
    ) {
        Text(
            modifier = Modifier.padding(start = 16.dp, top = 16.dp),
            text = stringResource(R.string.photos),
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
        )

        LazyRow(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            items(state.photos) {
                Box(
                    modifier = Modifier
                        .width(80.dp)
                        .height(70.dp)
                        .padding(end = 10.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .border(BorderStroke(width = 4.dp, color = Color.LightGray))
                        .clickable { onOpenFullScreenImage.invoke(it) }
                ) {
                    AsyncImage(
                        model = it.url,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            if (state.photos.size < MAX_NUMBER_OF_PHOTOS) {
                item {
                    Box(modifier = Modifier
                        .size(70.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .border(BorderStroke(width = 4.dp, color = Color.LightGray))
                        .clickable { onOpenGallery.invoke() }
                    ) {
                        Icon(
                            modifier = Modifier.align(Alignment.Center),
                            imageVector = Icons.Default.Add,
                            contentDescription = "add photo",
                            tint = Color.LightGray
                        )
                    }
                }
            }
        }
    }
}