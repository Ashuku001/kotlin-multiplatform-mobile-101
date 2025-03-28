package com.example.socialapp.android

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
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
import com.example.socialapp.android.destinations.ProfileDestination
import com.example.socialapp.android.destinations.loginDestination
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.utils.currentDestinationAsState

// Root application to setup navigation
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SocialApp(
    uiState: MainActivityUiState
) {
    val navHostController = rememberNavController() // navigation controller
    val snackbarHostState = remember { SnackbarHostState() }
    val systemUiController = rememberSystemUiController()

    val isSystemInDark = isSystemInDarkTheme()
    val statusBarColor = if (isSystemInDark) {
        MaterialTheme.colorScheme.surface
    } else {
        MaterialTheme.colorScheme.primary
    }
    SideEffect {
        systemUiController.setStatusBarColor(color = statusBarColor, darkIcons =  !isSystemInDark)
    }

    Scaffold (
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            AppBar(
                navHostController = navHostController,
                uiState = uiState,
                onProfileNavigation =if (uiState is MainActivityUiState.Success) {
                    { navHostController.navigate(ProfileDestination(uiState.currentUser.id)) }
                } else {
                    {}
                }
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = navHostController.currentDestinationAsState().value == HomeDestination
            ) {
                FloatingActionButton(
                    onClick = {
                        navHostController.navigate(
                            route = "create_post"
                        )
                    },
                    modifier = Modifier.background(MaterialTheme.colorScheme.primary)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    ){ innerPaddings ->
        DestinationsNavHost(
            modifier = Modifier.padding(innerPaddings),
            navGraph = NavGraphs.root,
            navController = navHostController
        )
    }
    // when
    when(uiState){
        MainActivityUiState.Loading -> {} // don't do anything if loading
        is MainActivityUiState.Success -> {
            LaunchedEffect(key1 = Unit) { // react
                if(uiState.currentUser.token.isEmpty()) { // by checking if there is a token
                    navHostController.navigate(loginDestination.route) {
                        popUpTo(HomeDestination.route) {
                            inclusive = true
                        }
                    }
                }
            }
        }
    }

}