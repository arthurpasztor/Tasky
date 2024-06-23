package com.example.tasky.agenda.presentation.composables.utils

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tasky.ui.theme.VeryLightGray

@Preview
@Composable
fun HorizontalDividerGray1dp() {
    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = VeryLightGray, thickness = 1.dp)
}

