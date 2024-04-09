package com.example.tasky

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.example.tasky.auth.presentation.LoginRoot
import com.example.tasky.destinations.LoginRootDestination
import com.example.tasky.main.data.ApiRepository
import com.example.tasky.ui.theme.BackgroundBlack
import com.example.tasky.ui.theme.TaskyTheme
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.manualcomposablecalls.composable
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject

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
                            checkSession()
                            LoginRoot(destinationsNavigator)
                        }
                    }
                }
            }

            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    private val repository: ApiRepository by inject(ApiRepository::class.java)

    private fun checkSession() {
        //TODO make the check during the splash screen
        lifecycleScope.launch {
            val response = repository.authenticate()
            Log.e("-------", "checkSession: $response")
        }
    }
}
