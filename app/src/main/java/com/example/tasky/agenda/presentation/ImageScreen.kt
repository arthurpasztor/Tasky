package com.example.tasky.agenda.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.tasky.R
import com.example.tasky.agenda.presentation.composables.utils.CloseButton
import com.example.tasky.ui.theme.headerStyle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.result.ResultBackNavigator

@Destination<RootGraph>
@Composable
fun ImageScreenRoot(
    resultNavigator: ResultBackNavigator<String>,
    key: String,
    uri: String
) {
    ImageScreen(
        uri = uri,
        onBackNavigation = {
            resultNavigator.navigateBack()
        },
        onRemoveImage = {
            resultNavigator.navigateBack(result = key)
        }
    )
}

@Preview
@Composable
fun ImageScreen(
    uri: String = "",
    onBackNavigation: () -> Unit = {},
    onRemoveImage: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Row(modifier = Modifier) {
            CloseButton { onBackNavigation.invoke() }
            Spacer(Modifier.weight(1f))
            Text(
                modifier = Modifier
                    .align(Alignment.CenterVertically),
                text = stringResource(id = R.string.photo),
                style = headerStyle
            )
            Spacer(Modifier.weight(1f))
            IconButton(
                modifier = Modifier
                    .size(60.dp)
                    .padding(8.dp),
                onClick = {
                    onRemoveImage.invoke()
                }) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = "remove photo",
                    tint = Color.White,
                )
            }
        }

        Box(modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .padding(16.dp)) {
        AsyncImage(
            model = uri,
            contentDescription = null,
            modifier = Modifier.clip(RoundedCornerShape(2)),
            contentScale = ContentScale.Crop
        )}
    }
}

