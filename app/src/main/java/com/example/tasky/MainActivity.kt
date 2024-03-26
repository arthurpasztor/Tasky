package com.example.tasky

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.tasky.auth.presentation.LoginRoot
import com.example.tasky.auth.presentation.NavGraphs
import com.example.tasky.auth.presentation.destinations.LoginRootDestination
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
                        composable(LoginRootDestination) {
                            LoginRoot(destinationsNavigator)
                        }
                    }
                }
            }

            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }
}
