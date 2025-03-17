package com.example.socialapp.android.auth.signup

import androidx.compose.runtime.Composable
import com.example.socialapp.android.auth.destinations.loginDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.koinViewModel

// NavHost determines this is the start destination
@Destination(start = true)
@Composable
fun signup(
    navigator: DestinationsNavigator
) {
    val viewModel: SignupViewModel = koinViewModel()
    SignupScreen(
        uiState = viewModel.uiState,
        onEmailChange = viewModel::updateEmail,
        onPasswordChange = viewModel::updatePassword,
        onUsernameChange = viewModel::updateUsername,
        onNavigateToLogin = {
            navigator.navigate(loginDestination)
        }
    )
}