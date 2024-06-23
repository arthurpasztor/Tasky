package com.example.tasky.auth.presentation.util

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.example.tasky.R
import com.example.tasky.ui.theme.TaskyGreen

@Preview
@Composable
fun UserInfoTextField(
    modifier: Modifier = Modifier,
    input: String = "",
    label: String = "Email address",
    isValid: Boolean = true,
    validationErrorText: String? = "Error",
    updateInputState: (String) -> Unit = {}
) {
    val emailCheckIcon: @Composable (() -> Unit) =
        {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "checkIcon",
                tint = TaskyGreen
            )
        }

    val horizontalPadding = dimensionResource(R.dimen.padding_20)
    val cornerRadius = dimensionResource(R.dimen.radius_10)

    TextField(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = horizontalPadding, end = horizontalPadding)
            .clip(RoundedCornerShape(cornerRadius, cornerRadius, cornerRadius, cornerRadius)),
        value = input,
        onValueChange = updateInputState,
        label = { Text(text = label) },
        trailingIcon = if (isValid) emailCheckIcon else null,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        singleLine = true,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedSupportingTextColor = Color.Red,
            unfocusedSupportingTextColor = Color.Red,
        ),
        supportingText = validationErrorText?.let {
            @Composable {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    )
}