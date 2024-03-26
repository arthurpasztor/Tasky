package com.example.tasky.auth.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.tasky.R
import com.ramcosta.composedestinations.annotation.Destination

@Destination
@Preview
@Composable
fun MainScreen() {
    val fontSize = dimensionResource(R.dimen.font_size_36).value.sp

    Text(
        text = "Successful login!",
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.Bold,
        fontSize = fontSize,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray)
            .wrapContentHeight(align = Alignment.CenterVertically),
        color = Color.White
    )
}
