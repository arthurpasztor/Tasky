package com.example.tasky.main.presentation

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tasky.NavGraphs
import com.example.tasky.R
import com.example.tasky.auth.domain.HttpError
import com.example.tasky.auth.domain.Result
import com.example.tasky.auth.domain.asUiText
import com.example.tasky.destinations.LoginRootDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.koinViewModel

// FYI, MainScreen is for testing purposes only, will be changed
@Destination
@Composable
fun MainRoot(navigator: DestinationsNavigator) {

    val TAG = "MainScreen"

    val context = LocalContext.current
    val viewModel: MainViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel, context) {
        viewModel.navChannel.collect { destination ->
            when (destination) {
                is MainResponseAction.HandleLogoutResponse -> {
                    when (destination.result) {
                        is Result.Success -> {
                            viewModel.onAction(MainAction.ClearUserData)
                            navigator.clearBackStack(NavGraphs.root)
                            navigator.navigate(LoginRootDestination)
                        }
                        is Result.Error -> {
                            val errorMessage = (destination.result.error as HttpError).asUiText().asString(context)
                            Log.e(TAG, "Error: $errorMessage")
                            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    MainScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Preview
@Composable
private fun MainScreen(
    state: MainState = MainState("User"),
    onAction: (MainAction) -> Unit = {}
) {
    val fontSize = dimensionResource(R.dimen.font_size_36).value.sp

    Column {
        Button(
            modifier = Modifier
                .height(100.dp)
                .fillMaxWidth(),
            onClick = {
                onAction.invoke(MainAction.LogOut)
            }) {
            Text(
                text = "Log out",
                color = Color.White,
                fontSize = fontSize,
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            text = "Welcome ${state.userName}",
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
}

sealed class MainAction {
    data object LogOut: MainAction()
    data object ClearUserData: MainAction()
}