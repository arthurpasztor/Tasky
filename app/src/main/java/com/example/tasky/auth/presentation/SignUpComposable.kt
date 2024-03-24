package com.example.tasky.auth.presentation

import android.os.Parcelable
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tasky.R
import com.example.tasky.ui.theme.BackgroundBlack
import com.example.tasky.ui.theme.BackgroundWhite
import com.ramcosta.composedestinations.annotation.Destination
import kotlinx.parcelize.Parcelize

data class SignUpComposableNavArgs(
    val state: SignUpState
)

@Preview
@Destination
@Composable
fun SignUpComposable(
    state: SignUpState = SignUpState(),
    onAction: (SignUpAction) -> Unit = {}
) {

    val context = LocalContext.current

    val cornerRadius = dimensionResource(R.dimen.radius_30)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundBlack)
    ) {
        AuthenticationTitleComposable(stringResource(R.string.create_your_account))
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(cornerRadius, cornerRadius, 0.dp, 0.dp))
                .background(BackgroundWhite)
        ) {
            UserInfoTextField(
                modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_40)),
                input = state.nameText,
                label = stringResource(R.string.name),
                isValid = state.isNameValid(),
                updateInputState = { state.nameText = it }
            )
            UserInfoTextField(
                modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_20)),
                input = state.emailText,
                label = stringResource(R.string.email),
                isValid = state.isEmailValid(),
                updateInputState = { state.emailText = it }
            )
            PasswordTextField(
                modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_20)),
                input = state.passwordText,
                updateInputState = { state.passwordText = it }
            )
            ActionButton(
                modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_40)),
                text = stringResource(R.string.get_started)
            ) {
                when {
                    !state.isNameValid() -> Toast.makeText(context, R.string.error_name_invalid, Toast.LENGTH_LONG).show()
                    !state.isEmailValid() -> Toast.makeText(context, R.string.error_email_invalid, Toast.LENGTH_LONG).show()
                    !state.isPasswordValid() -> Toast.makeText(context, R.string.error_password_invalid, Toast.LENGTH_LONG).show()
                    else -> onAction.invoke(SignUpAction.SIGN_UP)
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            BackButton(Modifier.padding(bottom = dimensionResource(id = R.dimen.padding_40))) {
                onAction.invoke(SignUpAction.NAVIGATE_BACK)
            }
        }
    }
}

@Composable
fun BackButton(modifier: Modifier = Modifier, action: () -> Unit) {
    val horizontalPadding = dimensionResource(R.dimen.padding_20)
    val cornerRadius = dimensionResource(R.dimen.radius_10)

    Button(
        modifier = modifier
            .padding(start = horizontalPadding),
        onClick = { action() },
        colors = ButtonDefaults.buttonColors(containerColor = BackgroundBlack),
        shape = RoundedCornerShape(cornerRadius)
    ) {
        Icon(
            imageVector = Icons.Default.ArrowBackIosNew,
            contentDescription = "checkIcon",
            tint = Color.White
        )
    }
}

@Parcelize
enum class SignUpAction: Parcelable {
    SIGN_UP,
    NAVIGATE_BACK
}
