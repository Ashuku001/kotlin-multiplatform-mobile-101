package com.example.socialapp.android

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.socialapp.android.auth.NavGraphs
import com.ramcosta.composedestinations.DestinationsNavHost

// Root application to setup navigation
@Composable
fun SocialApp() {
    val navHostController = rememberNavController() // navigation controller

    // pass control that determines which screen to display i.e., composable with @Destination decorator
    DestinationsNavHost(
        navGraph = NavGraphs.root,
        navController = navHostController
    )
}