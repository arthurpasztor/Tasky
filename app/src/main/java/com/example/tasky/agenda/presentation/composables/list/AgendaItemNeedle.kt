package com.example.tasky.agenda.presentation.composables.list

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

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