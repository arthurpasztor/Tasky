package com.example.tasky

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.tasky.core.presentation.RootScreen
import com.example.tasky.destinations.RootScreenDestination
import com.example.tasky.ui.theme.BackgroundBlack
import com.example.tasky.ui.theme.TaskyTheme
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.manualcomposablecalls.composable

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TaskyTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = BackgroundBlack) {
                    DestinationsNavHost(
                        navGraph = NavGraphs.root
                    ) {
                        composable(RootScreenDestination) {
                            RootScreen(navigator = destinationsNavigator)
                        }
                    }
                }
            }

            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }
}
