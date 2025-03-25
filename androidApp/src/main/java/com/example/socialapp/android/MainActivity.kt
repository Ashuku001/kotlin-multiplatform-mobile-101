package com.example.socialapp.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.socialapp.android.common.theming.SocialAppTheme
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

// the entry point to the application (App launch when user clicks App Icon)
class MainActivity : ComponentActivity() {
    private val viewModel: MainActivityViewModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        val uiState: MutableState<MainActivityUiState> = mutableStateOf(MainActivityUiState.Loading)

        // since we are not in a composable launch a new Coroutine
        lifecycleScope.launch { // cancel collecting flow when activity is destroyed
            // make flow lifecycle aware. suspend execution until the the Activity state is STARTED
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState // this flow
                    .onEach {
                        uiState.value = it // update the ui state e.g., loading to success
                    }
                    .collect()
            }
        }

        // keep the splash screen on screen if the MainActivity is still loading
        splashScreen.setKeepOnScreenCondition {
            uiState.value == MainActivityUiState.Loading
        }

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
                    SocialApp(uiState.value) // The entry point to setup navigation
                }
            }
        }
    }
}