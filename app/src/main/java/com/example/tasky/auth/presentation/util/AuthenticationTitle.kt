package com.example.tasky.auth.presentation.util

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.tasky.R

@Preview
@Composable
fun AuthenticationTitle(title: String = "Welcome Back!") {
    val verticalPadding = dimensionResource(R.dimen.padding_52)
    val fontSize = dimensionResource(R.dimen.font_size_36).value.sp

    Text(
        text = title,
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.Bold,
        fontSize = fontSize,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = verticalPadding),
        color = Color.White
    )
}