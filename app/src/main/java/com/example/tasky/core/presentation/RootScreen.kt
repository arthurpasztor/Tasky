package com.example.tasky.core.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.example.tasky.auth.domain.Result
import com.example.tasky.destinations.LoginRootDestination
import com.example.tasky.destinations.MainRootDestination
import com.example.tasky.destinations.RootScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.koinViewModel

@RootNavGraph(start = true)
@Destination
@Composable
fun RootScreen(navigator: DestinationsNavigator)  {

    val context = LocalContext.current
    val viewModel: RootViewModel = koinViewModel()

    LaunchedEffect(viewModel, context) {
        viewModel.navChannel.collect { result ->
            when (result) {
                is Result.Success -> {
                    navigator.navigate(MainRootDestination) {
                        popUpTo(RootScreenDestination.route) {
                            inclusive = true
                        }
                    }
                }
                is Result.Error -> {
                    navigator.navigate(LoginRootDestination) {
                        popUpTo(RootScreenDestination.route) {
                            inclusive = true
                        }
                    }
                }
            }
        }
    }

    viewModel.checkTokenValid()
}