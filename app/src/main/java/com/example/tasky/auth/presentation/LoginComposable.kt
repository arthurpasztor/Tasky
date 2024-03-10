package com.example.tasky.auth.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tasky.R
import com.example.tasky.auth.domain.LoginViewModel
import com.example.tasky.ui.theme.BackgroundBlack
import com.example.tasky.ui.theme.BackgroundWhite

@Preview
@Composable
fun LoginComposable(viewModel: LoginViewModel = viewModel()) {
    val cornerRadius = dimensionResource(R.dimen.radius_30)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundBlack)
    ) {
        AuthenticationTitleComposable(stringResource(R.string.welcome_back))
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(cornerRadius, cornerRadius, 0.dp, 0.dp))
                .background(BackgroundWhite)
        ) {
            EmailTextField(
                modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_40)),
                input = viewModel.emailText,
                label = stringResource(R.string.email),
                isValid = { viewModel.isEmailValid() },
                updateInputState = { viewModel.updateEmail(it) }
            )
            PasswordTextField(
                modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_20)),
                input = viewModel.passwordText,
                updateInputState = { viewModel.updatePassword(it) }
            )
            ActionButton(
                modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_20)),
                text = stringResource(R.string.log_in)
            ) {
                viewModel.logIn()
            }
        }
    }
}
