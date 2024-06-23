package com.example.tasky.agenda.presentation.composables.utils

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

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