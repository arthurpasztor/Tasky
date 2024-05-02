package com.example.tasky.main.presentation

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tasky.R
import com.example.tasky.auth.domain.HttpError
import com.example.tasky.auth.domain.Result
import com.example.tasky.auth.domain.asUiText
import com.example.tasky.destinations.AgendaRootDestination
import com.example.tasky.destinations.LoginRootDestination
import com.example.tasky.ui.theme.BackgroundBlack
import com.example.tasky.ui.theme.BackgroundWhite
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.koinViewModel
import java.util.Locale

@Destination
@Composable
fun AgendaRoot(navigator: DestinationsNavigator) {

    val TAG = "AgendaScreen"

    val context = LocalContext.current
    val viewModel: AgendaViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(true) {
        viewModel.navChannel.collect { destination ->
            when (destination) {
                is AgendaResponseAction.HandleLogoutResponse -> {
                    when (destination.result) {
                        is Result.Success -> {
                            viewModel.onAction(AgendaAction.ClearUserData)
                            navigator.navigate(LoginRootDestination) {
                                popUpTo(AgendaRootDestination.route) {
                                    inclusive = true
                                }
                            }
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

    AgendaScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Preview
@Composable
private fun AgendaScreen(
    state: AgendaState = AgendaState("Arthur"),
    onAction: (AgendaAction) -> Unit = {}
) {
    val cornerRadius = dimensionResource(R.dimen.radius_30)
    val monthPadding = dimensionResource(R.dimen.padding_20)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundBlack)
    ) {
        Row {
            Text(
                modifier = Modifier
                    .padding(
                        start = monthPadding,
                        top = monthPadding,
                        bottom = monthPadding
                    ),
                text = state.month.uppercase(Locale.getDefault()),
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    fontSize = dimensionResource(R.dimen.font_size_16).value.sp,
                    color = Color.White
                )
            )
            Icon(
                modifier = Modifier.align(Alignment.CenterVertically),
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "arrowDropDownIcon",
                tint = Color.White
            )
            Spacer(Modifier.weight(1f))
            ProfileIcon(
                modifier = Modifier
                    .padding(end = dimensionResource(R.dimen.padding_8))
                    .align(Alignment.CenterVertically),
                state = state,
                onAction = onAction
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(cornerRadius, cornerRadius, 0.dp, 0.dp))
                .background(BackgroundWhite)
        ) {

        }
    }
}

sealed class AgendaAction {
    data object LogOut : AgendaAction()
    data object ClearUserData : AgendaAction()
}