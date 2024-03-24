package com.example.tasky.auth.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tasky.R
import com.example.tasky.auth.presentation.destinations.SignUpRootDestination
import com.example.tasky.ui.theme.BackgroundBlack
import com.example.tasky.ui.theme.BackgroundWhite
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.koinViewModel

@RootNavGraph(start = true)
@Destination
@Composable
fun LoginRoot(navigator: DestinationsNavigator) {

    val context = LocalContext.current
    val viewModel: LoginViewModel = koinViewModel()

    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(viewModel, context) {
        viewModel.navChannel.collect { destination ->
            when (destination) {
                LoginNav.NavigateToSignUpScreen -> navigator.navigate(SignUpRootDestination)
            }
        }
    }
    LoginScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Preview
@Composable
fun LoginScreen(
    state: LoginState = LoginState(),
    onAction: (LoginAction) -> Unit = {}
) {
    val cornerRadius = dimensionResource(R.dimen.radius_30)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundBlack)
    ) {
        AuthenticationTitle(stringResource(R.string.welcome_back))
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(cornerRadius, cornerRadius, 0.dp, 0.dp))
                .background(BackgroundWhite)
        ) {
            UserInfoTextField(
                modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_40)),
                input = state.emailText,
                label = stringResource(R.string.email),
                isValid = state.isEmailValid,
                updateInputState = { onAction(LoginAction.UpdateEmail(it)) }
            )
            PasswordTextField(
                modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_20)),
                input = state.passwordText,
                updateInputState = { onAction(LoginAction.UpdatePassword(it)) }
            )
            ActionButton(
                modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_20)),
                text = stringResource(R.string.log_in)
            ) {
                onAction.invoke(LoginAction.LogIn)
            }
            Spacer(modifier = Modifier.weight(1f))
            SignUpText(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = dimensionResource(id = R.dimen.padding_40)),
                onAction = onAction
            )
        }
    }
}

@Composable
fun SignUpText(
    modifier: Modifier = Modifier,
    onAction: (LoginAction) -> Unit = {}
) {
    val signUp = stringResource(id = R.string.sign_up).uppercase()

    val annotatedString = buildAnnotatedString {
        withStyle(style = SpanStyle(Color.LightGray)) {
            append(stringResource(id = R.string.dont_have_an_account).uppercase())
        }
        append(" ")
        withStyle(style = SpanStyle(Color.Blue)) {
            pushStringAnnotation(tag = signUp, annotation = signUp)
            append(signUp)
        }
    }

    ClickableText(
        text = annotatedString,
        modifier = modifier.background(BackgroundWhite),
        style = TextStyle(
            textAlign = TextAlign.Center,
            fontSize = dimensionResource(R.dimen.font_size_16).value.sp
        )
    ) { offset ->
        annotatedString.getStringAnnotations(offset, offset).firstOrNull()?.let {
            onAction.invoke(LoginAction.NavigateToSignUp)
        }
    }
}

sealed class LoginAction {
    class UpdateEmail(val email: String): LoginAction()
    class UpdatePassword(val password: String): LoginAction()
    data object LogIn: LoginAction()
    data object NavigateToSignUp: LoginAction()
}
