package com.example.socialapp.android.auth.signup

import androidx.compose.runtime.Composable
import com.example.socialapp.android.destinations.HomeDestination
import com.example.socialapp.android.destinations.loginDestination
import com.example.socialapp.android.destinations.signupDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.koinViewModel

// NavHost determines this is the start destination
@Destination
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
            navigator.navigate(loginDestination) {
                popUpTo(signupDestination.route) {
                    inclusive = true
                }
            }
        },
        onNavigateToHome = {
            navigator.navigate(HomeDestination) {
                popUpTo(signupDestination.route) {
                    inclusive = true
                }
            }
        },
        onSignupClick = viewModel::signUp,
    )
}