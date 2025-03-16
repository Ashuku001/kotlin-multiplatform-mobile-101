package com.example.socialapp.android

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.socialapp.android.auth.NavGraphs
import com.ramcosta.composedestinations.DestinationsNavHost

@Composable
fun SocialApp() {
    val navHostController = rememberNavController()

    DestinationsNavHost(navGraph = NavGraphs.root,
                        navController = navHostController
    )
}