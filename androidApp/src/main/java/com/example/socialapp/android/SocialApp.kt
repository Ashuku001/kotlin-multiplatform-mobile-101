package com.example.socialapp.android

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.socialapp.android.common.components.AppBar
import com.example.socialapp.android.destinations.HomeDestination
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ramcosta.composedestinations.DestinationsNavHost

// Root application to setup navigation
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SocialApp(
    token: String?
) {
    val navHostController = rememberNavController() // navigation controller
    val snackbarHostState = remember { SnackbarHostState() }
    val systemUiControler = rememberSystemUiController()

    val isSystemInDark = isSystemInDarkTheme()
    val statusBarColor = if (isSystemInDark) {
        MaterialTheme.colorScheme.surface
    } else {
        MaterialTheme.colorScheme.primary
    }
    SideEffect {
        systemUiControler.setStatusBarColor(color = statusBarColor, darkIcons =  !isSystemInDark)
    }

    Scaffold (
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            AppBar(navHostController = navHostController)
        }
        ) { innerPaddings ->  DestinationsNavHost(
        modifier = Modifier.padding(innerPaddings),
        navGraph = NavGraphs.root,
        navController = navHostController
    ) }

    // when
    LaunchedEffect(key1 = token, block = {
        // navigate to auth if there is no user token
        if (token != null && token.isEmpty()) {
            navHostController.navigate(HomeDestination.route) {
                // pop the backstack
                popUpTo(HomeDestination.route) {
                    inclusive = true
                }
            }
        }
    })

    // pass control that determines which screen to display i.e., composable with @Destination decorator

}