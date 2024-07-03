package com.example.tasky

import android.Manifest
import android.animation.ObjectAnimator
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.tasky.agenda.presentation.workmanager.AGENDA_ITEM_ID
import com.example.tasky.agenda.presentation.workmanager.AGENDA_ITEM_TYPE
import com.example.tasky.core.data.Preferences
import com.example.tasky.core.presentation.RootViewModel
import com.example.tasky.ui.theme.BackgroundBlack
import com.example.tasky.ui.theme.TaskyTheme
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.generated.NavGraphs
import com.ramcosta.composedestinations.generated.destinations.AgendaRootDestination
import com.ramcosta.composedestinations.generated.destinations.LoginRootDestination
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    private val prefs: Preferences by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel: RootViewModel by viewModel()

        installSplashScreen().apply {
            setKeepOnScreenCondition {
                viewModel.isCheckingAuthentication.value
            }
            setOnExitAnimationListener { screen ->
                ObjectAnimator.ofFloat(
                    screen.iconView,
                    View.SCALE_X,
                    0.4f,
                    0.0f
                ).apply {
                    interpolator = OvershootInterpolator()
                    duration = 500L
                    doOnEnd { screen.remove() }
                    start()
                }

                ObjectAnimator.ofFloat(
                    screen.iconView,
                    View.SCALE_Y,
                    0.4f,
                    0.0f
                ).apply {
                    interpolator = OvershootInterpolator()
                    duration = 500L
                    doOnEnd { screen.remove() }
                    start()
                }
            }
        }

        setContent {
            TaskyTheme {
                val isCheckingAuthentication by viewModel.isCheckingAuthentication.collectAsState()
                val isAuthenticated by viewModel.isLoggedIn.collectAsState()

                Surface(modifier = Modifier.fillMaxSize(), color = BackgroundBlack) {
                    if (!isCheckingAuthentication) {
                        DestinationsNavHost(
                            navGraph = NavGraphs.root,
                            startRoute = if (isAuthenticated) AgendaRootDestination else LoginRootDestination
                        )
                    }
                }

                NotificationPermissionsHandler()
            }

            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    @Composable
    private fun NotificationPermissionsHandler() {
        val context = LocalContext.current
        val containsNotificationInfoPermissionInfo = prefs.containsNotificationInfoPermissionInfo()

        if (!containsNotificationInfoPermissionInfo) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val status = ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                if (status != PackageManager.PERMISSION_GRANTED) {

                    val launcher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.RequestPermission(),
                        onResult = { isGranted ->
                            prefs.setNotificationsPermission(isGranted)
                            if (isGranted) {
                                Log.i(TAG, "Manifest.permission.POST_NOTIFICATIONS permission granted")
                            } else {
                                Log.i(TAG, "Manifest.permission.POST_NOTIFICATIONS permission denied")
                            }
                        }
                    )

                    SideEffect {
                        launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            } else {
                prefs.setNotificationsPermission(true)
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        handleNewIntent(intent)
    }

    private fun handleNewIntent(intent: Intent?) {
        val id = intent?.getStringExtra(AGENDA_ITEM_ID)
        val type = intent?.getStringExtra(AGENDA_ITEM_TYPE)

        Log.e(TAG, "onNewIntent: agenda item id: $id, type: $type")
        Log.e(TAG, "onNewIntent: intent extras: ${intent?.extras}")
        // TODO navigate to agenda item
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
