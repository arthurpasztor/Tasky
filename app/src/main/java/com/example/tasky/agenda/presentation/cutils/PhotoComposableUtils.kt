package com.example.tasky.agenda.presentation.cutils

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tasky.R
import com.example.tasky.ui.theme.BackgroundGray
import com.example.tasky.ui.theme.VeryLightGray

@Preview
@Composable
fun AddPhotoEmptySpace() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .border(BorderStroke(1.dp, BackgroundGray))
            .background(BackgroundGray),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "plus",
            tint = Color.LightGray
        )
        Text(
            modifier = Modifier.padding(start = 12.dp),
            text = stringResource(id = R.string.add_photos),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.LightGray
        )

        Spacer(modifier = Modifier.weight(1f))
    }

    HorizontalDivider(
        modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp),
        color = VeryLightGray,
        thickness = 1.dp
    )
}
