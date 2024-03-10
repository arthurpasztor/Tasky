package com.example.tasky

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.tasky.auth.presentation.LoginComposable
import com.example.tasky.ui.theme.BackgroundBlack
import com.example.tasky.ui.theme.TaskyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TaskyTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = BackgroundBlack) {
                    LoginComposable()
                }
            }
        }
    }
}
