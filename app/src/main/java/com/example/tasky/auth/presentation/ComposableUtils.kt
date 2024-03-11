package com.example.tasky.auth.presentation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tasky.R
import com.example.tasky.ui.theme.BackgroundBlack
import com.example.tasky.ui.theme.CheckmarkGreen
import java.util.Locale

@Preview
@Composable
fun AuthenticationTitleComposable(title: String = "Welcome Back!") {
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

@Preview
@Composable
fun UserInfoTextField(
    modifier: Modifier = Modifier,
    input: String = "",
    label: String = "Email address",
    isValid: () -> Boolean = { true },
    updateInputState: (String) -> Unit = {}
) {
    val emailCheckIcon: @Composable (() -> Unit) =
        {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "checkIcon",
                tint = CheckmarkGreen
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
        trailingIcon = if (isValid()) emailCheckIcon else null,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        singleLine = true,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}

@Preview
@Composable
fun PasswordTextField(
    modifier: Modifier = Modifier,
    input: String = "",
    updateInputState: (String) -> Unit = {}
) {
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    val horizontalPadding = dimensionResource(R.dimen.padding_20)
    val cornerRadius = dimensionResource(R.dimen.radius_10)

    TextField(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = horizontalPadding, end = horizontalPadding)
            .clip(RoundedCornerShape(cornerRadius, cornerRadius, cornerRadius, cornerRadius)),
        value = input,
        onValueChange = updateInputState,
        label = { Text(text = stringResource(R.string.password)) },
        trailingIcon = {
            val image = if (passwordVisible)
                Icons.Filled.Visibility
            else Icons.Filled.VisibilityOff

            val description = if (passwordVisible) "Hide password" else "Show password"
            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(imageVector = image, description)
            }
        },
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        singleLine = true,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}

@Preview
@Composable
fun ActionButton(
    modifier: Modifier = Modifier,
    text: String = "Log in",
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
        shape = RoundedCornerShape(cornerRadius)
    ) {
        Text(
            text = text.uppercase(Locale.getDefault()),
            color = Color.White,
            fontSize = fontSize,
            fontWeight = FontWeight.Bold
        )
    }
}
