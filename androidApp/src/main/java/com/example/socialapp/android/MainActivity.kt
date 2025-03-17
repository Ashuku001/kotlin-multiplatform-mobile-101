package com.example.socialapp.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import com.example.socialapp.android.common.theming.SocialAppTheme

// the entry point to the application (App launch when user clicks App Icon)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // build the UI
        setContent {
            // theme consistency
            SocialAppTheme  {
                // full screen
                Surface(
                    modifier = Modifier.fillMaxSize(), // fill the screen
                    color = MaterialTheme.colorScheme.background // with this color
                ) {
                    SocialApp() // The entry point to setup navigation
                }
            }
        }
    }
}