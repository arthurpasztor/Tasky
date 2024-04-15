package com.example.tasky

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.tasky.core.presentation.RootViewModel
import com.example.tasky.destinations.LoginRootDestination
import com.example.tasky.destinations.MainRootDestination
import com.example.tasky.ui.theme.BackgroundBlack
import com.example.tasky.ui.theme.TaskyTheme
import com.ramcosta.composedestinations.DestinationsNavHost
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel: RootViewModel by inject()

        installSplashScreen().apply {
            setKeepOnScreenCondition {
                viewModel.isLoggedIn.value != null
            }
        }

        setContent {
            TaskyTheme {
                val isAuthenticated by viewModel.isLoggedIn.collectAsState()

                Surface(modifier = Modifier.fillMaxSize(), color = BackgroundBlack) {
                    DestinationsNavHost(
                        navGraph = NavGraphs.root,
                        startRoute = if (isAuthenticated == true) MainRootDestination else LoginRootDestination
                    )
                }
            }

            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }
}
