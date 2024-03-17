package com.example.tasky

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tasky.auth.presentation.LoginViewModel
import com.example.tasky.auth.presentation.SignUpViewModel
import com.example.tasky.auth.presentation.LoginAction
import com.example.tasky.auth.presentation.LoginComposable
import com.example.tasky.auth.presentation.NavGraphs
import com.example.tasky.auth.presentation.destinations.LoginComposableDestination
import com.example.tasky.auth.presentation.destinations.SignUpComposableDestination
import com.example.tasky.ui.theme.BackgroundBlack
import com.example.tasky.ui.theme.TaskyTheme
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.manualcomposablecalls.composable
import com.ramcosta.composedestinations.scope.AnimatedDestinationScope

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val loginVM: LoginViewModel = viewModel()
            val signUpVM: SignUpViewModel = viewModel()

            TaskyTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = BackgroundBlack) {
                    DestinationsNavHost(
                        navGraph = NavGraphs.root
                    ) {
                        composable(LoginComposableDestination) {
                            LoginComposable(
                                vm = loginVM,
                                state = remember { loginVM.state },
                            ) { action ->
                                when (action) {
                                    LoginAction.LOG_IN -> loginVM.logIn()
                                    LoginAction.NAVIGATE_TO_SIGN_UP -> {
                                        navigateToSignUp(signUpVM)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    private fun AnimatedDestinationScope<Unit>.navigateToSignUp(signUpVM: SignUpViewModel) {
        destinationsNavigator.navigate(
            SignUpComposableDestination(
                state = signUpVM.state,
                //TODO make lambda argument generated in the *Destination class
            )
        )
    }
}
