package com.example.tasky.auth.presentation.util

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tasky.R
import com.example.tasky.ui.theme.BackgroundBlack
import java.util.Locale

@Preview
@Composable
fun ActionButton(
    modifier: Modifier = Modifier,
    text: String = "Log in",
    enabled: Boolean = true,
    action: () -> Unit = {}
) {
    val horizontalPadding = dimensionResource(R.dimen.padding_20)
    val cornerRadius = dimensionResource(R.dimen.radius_30)
    val fontSize = dimensionResource(R.dimen.font_size_16).value.sp

    Button(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .padding(start = horizontalPadding, end = horizontalPadding),
        onClick = { action.invoke() },
        colors = ButtonDefaults.buttonColors(containerColor = BackgroundBlack),
        shape = RoundedCornerShape(cornerRadius),
        enabled = enabled
    ) {
        Text(
            text = text.uppercase(Locale.getDefault()),
            color = Color.White,
            fontSize = fontSize,
            fontWeight = FontWeight.Bold
        )
    }
}
