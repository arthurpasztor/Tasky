package com.example.tasky.core.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.example.tasky.NavGraphs
import com.example.tasky.auth.domain.Result
import com.example.tasky.destinations.LoginRootDestination
import com.example.tasky.destinations.MainRootDestination
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
                    navigator.clearBackStack(NavGraphs.root)
                    navigator.navigate(MainRootDestination)
                }
                is Result.Error -> {
                    navigator.clearBackStack(NavGraphs.root)
                    navigator.navigate(LoginRootDestination)
                }
            }
        }
    }

    viewModel.checkTokenValid()
}